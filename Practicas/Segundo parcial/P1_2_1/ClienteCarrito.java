package P1_2_1;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClienteCarrito extends JFrame {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9090;
    private Catalogo catalogo;
    private JPanel panelProductos;
    private JPanel panelCarrito;
    private Map<Integer, Integer> carrito; // Clave: ID del producto, Valor: Cantidad

    public ClienteCarrito() {
        super("PetShop - Artículos para mascotas");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        cargarCatalogoYArchivos();
        carrito = new HashMap<>();

        mostrarProductos();
        mostrarBotones();
    }

    private void cargarCatalogoYArchivos() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            catalogo = (Catalogo) ois.readObject();
            recibirArchivos(socket);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void recibirArchivos(Socket socket) throws IOException {
        File carpeta = new File("Catalogo");
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }

        DataInputStream dis = new DataInputStream(socket.getInputStream());
        while (true) {
            String nombreArchivo = dis.readUTF();
            if (nombreArchivo.equals("FIN")) {
                break;
            }
            long tamañoArchivo = dis.readLong();

            File archivo = new File(carpeta, nombreArchivo);
            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                byte[] buffer = new byte[4096];
                int leidos;
                long bytesRecibidos = 0;

                while (bytesRecibidos < tamañoArchivo && (leidos = dis.read(buffer)) != -1) {
                    fos.write(buffer, 0, leidos);
                    bytesRecibidos += leidos;
                }
            }
        }
    }

    private void mostrarProductos() {
        panelProductos = new JPanel(new GridLayout(0, 3, 3, 3));
        JScrollPane scrollPane = new JScrollPane(panelProductos);
        add(scrollPane, BorderLayout.CENTER);

        for (Producto producto : catalogo.obtenerProductos()) {
            JPanel panelProducto = new JPanel(new BorderLayout()); // BorderLayout para posicionar la imagen en el centro
            JLabel labelImagen = new JLabel();
            ImageIcon imagen = createScaledImageIcon("Catalogo/" + producto.getImagenRuta(), 150, 150); // Especifica el nuevo ancho y alto deseados
            if (imagen != null) {
                labelImagen.setIcon(imagen);
            } else {
                labelImagen.setText("Error al cargar la imagen...");
                labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
                labelImagen.setVerticalAlignment(SwingConstants.CENTER);
            }

            JLabel labelNombre = new JLabel(producto.getNombre());
            JLabel labelPrecio = new JLabel("$" + producto.getPrecio());
            JLabel labelExistencias = new JLabel("Existencias: " + producto.getExistencias()); // Agregamos la etiqueta de existencias


            JPanel panelNombrePrecio = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Alineación central y espacio interno de 10 píxeles
            panelNombrePrecio.add(labelNombre);
            panelNombrePrecio.add(labelPrecio);
            panelNombrePrecio.add(labelExistencias);

            JButton botonAgregar = new JButton("Agregar al carrito");
            botonAgregar.addActionListener(e -> agregarAlCarrito(producto.getId()));
            botonAgregar.setPreferredSize(new Dimension(10, 30)); // Establecer el tamaño del botón

            panelProducto.add(labelImagen, BorderLayout.NORTH);
            panelProducto.add(panelNombrePrecio, BorderLayout.CENTER);
            panelProducto.add(botonAgregar, BorderLayout.SOUTH);

            panelProductos.add(panelProducto);
        }
    }

    // Método auxiliar para crear un ImageIcon redimensionado
    private ImageIcon createScaledImageIcon(String path, int width, int height) {
        java.net.URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(image);
        } else {
            System.err.println("No se pudo cargar la imagen: " + path);
            return null;
        }
    }

    private void mostrarBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton VerCarrito = new JButton("Ver carrito");
        VerCarrito.addActionListener(e -> mostrarCarrito());
        panelBotones.add(VerCarrito);

        JButton Comprar = new JButton("Comprar"); // Cambiamos el nombre del botón
        Comprar.addActionListener(e -> mostrarRecibo()); // Corregimos la acción del botón
        panelBotones.add(Comprar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void mostrarRecibo() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío. Agrega productos antes de realizar la compra.", "Carrito Vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder recibo = new StringBuilder();
        double total = 0;

        recibo.append("Recibo de compra:\n\n");

        for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
            int productoId = entry.getKey();
            int cantidad = entry.getValue();
            Producto producto = catalogo.obtenerProductoPorId(productoId).orElse(null);
            if (producto != null) {
                double subtotal = producto.getPrecio() * cantidad;
                recibo.append(producto.getNombre()).append(" x ").append(cantidad).append(" - $").append(subtotal).append("\n");
                total += subtotal;
            }
        }

        recibo.append("\nTotal a pagar: $").append(total);

        // Agregar botón para efectuar la compra
        JButton botonComprar = new JButton("Comprar");
        botonComprar.addActionListener(e -> {
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                oos.writeObject(carrito); // Enviamos el carrito al servidor
                oos.flush();
                try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())){
                    //Esperamos la respuesta del servidor
                    String respuesta = (String) ois.readObject();
                    JOptionPane.showMessageDialog(this, respuesta, "Respuesta del servidor", JOptionPane.INFORMATION_MESSAGE);
                } catch (ClassNotFoundException ex){
                    ex.printStackTrace();
                }
            } catch (IOException exx) {
                JOptionPane.showMessageDialog(this, "Error al enviar los datos al servidor: " + exx.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                exx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Compra realizada con éxito. Las existencias han sido actualizadas.", "Compra Realizada", JOptionPane.INFORMATION_MESSAGE);
            // Limpiar el carrito después de la compra
            carrito.clear();
            // Actualizar la vista del carrito
            mostrarCarrito();

        });

        JPanel panelRecibo = new JPanel();
        panelRecibo.setLayout(new BoxLayout(panelRecibo, BoxLayout.Y_AXIS));
        panelRecibo.add(new JScrollPane(new JTextArea(recibo.toString())));
        panelRecibo.add(botonComprar); // Agregar el botón al panel

        JOptionPane.showMessageDialog(this, panelRecibo, "Recibo de compra", JOptionPane.INFORMATION_MESSAGE);
    }


    private void mostrarCarrito() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Carrito Vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (panelCarrito != null) {
            remove(panelCarrito); // Eliminamos el panel del carrito si ya existe
        }
        panelCarrito = new JPanel(new GridLayout(0, 1, 5, 2));

        double total = 0;
        for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
            Optional<Producto> optionalProducto = catalogo.obtenerProductoPorId(entry.getKey());
            if (optionalProducto.isPresent()) {
                Producto producto = optionalProducto.get();
                double subtotal = producto.getPrecio() * entry.getValue();
                total += subtotal;

                JPanel panelItem = new JPanel(new BorderLayout());
                JLabel labelProducto = new JLabel(producto.getNombre() + " x " + entry.getValue() + " - $" + subtotal);
                panelItem.add(labelProducto, BorderLayout.NORTH);
                panelCarrito.add(panelItem);
            }
        }

        // Agregamos el panel del carrito al BorderLayout
        add(panelCarrito, BorderLayout.EAST);

        // Volvemos a validar y repintar para reflejar los cambios
        revalidate();
        repaint();
    }

    private void agregarAlCarrito(int productoId) {
        Optional<Producto> optionalProducto = catalogo.obtenerProductoPorId(productoId);
        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            if (producto.getExistencias() > 0) {
                int nuevaCantidad = carrito.getOrDefault(productoId, 0) + 1;
                carrito.put(productoId, nuevaCantidad);
                producto.setExistencias(producto.getExistencias() - 1); // Reduce la existencia en el catálogo
                JOptionPane.showMessageDialog(this, "Producto agregado al carrito: " + producto.getNombre());
            } else {
                JOptionPane.showMessageDialog(this, "Producto no disponible o no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Producto no disponible o no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new ClienteCarrito().setVisible(true));
    }
}


