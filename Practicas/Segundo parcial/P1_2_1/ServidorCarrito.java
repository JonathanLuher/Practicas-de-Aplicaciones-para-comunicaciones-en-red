package P1_2_1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class ServidorCarrito {
    private static final int PUERTO = 9090;
    private Catalogo catalogo;

    public ServidorCarrito() {
        cargarCatalogo();
    }

    private void cargarCatalogo() {
        File archivoCatalogo = new File("catalogo.dat");
        if (archivoCatalogo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivoCatalogo))) {
                catalogo = (Catalogo) ois.readObject();
                System.out.println("Catálogo cargado correctamente.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar el catálogo: " + e.getMessage());
                catalogo = new Catalogo(); // Crea un nuevo catálogo vacío si no se puede cargar
            }
        } else {
            System.out.println("No se encontró el archivo de catálogo. Inicializando un catálogo vacío.");
            catalogo = new Catalogo(); // Inicializa un catálogo vacío
        }
    }

    public void iniciar() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en el puerto " + PUERTO + ", esperando conexiones...");
            while (true) {
                try (Socket cliente = servidor.accept()) {
                    System.out.println("Conexión establecida con " + cliente.getInetAddress().getHostAddress());
                    enviarCatalogo(cliente);
                    enviarArchivos(cliente);
                    manejarCliente(cliente);
                } catch (IOException e) {
                    System.err.println("Error al manejar la conexión del cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    private void enviarCatalogo(Socket cliente) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(cliente.getOutputStream());
        oos.writeObject(catalogo); // Envía el catálogo al cliente
        oos.flush();
    }

    private void enviarArchivos(Socket cliente) throws IOException {
        DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
        for (Producto producto : catalogo.obtenerProductos()) {
            enviarArchivo(dos, producto.getImagenRuta());
        }
        dos.writeUTF("FIN");
        dos.flush();
    }

    private void enviarArchivo(DataOutputStream dos, String rutaArchivo) throws IOException {
        File archivo = new File(rutaArchivo);
        if (archivo.exists() && archivo.isFile()) {
            FileInputStream fis = new FileInputStream(archivo);

            dos.writeUTF(archivo.getName());
            dos.writeLong(archivo.length());

            byte[] buffer = new byte[4096];
            int leidos;
            while ((leidos = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, leidos);
            }

            dos.flush();
            fis.close();
        }
    }

    // Después de recibir la conexión del cliente y antes de enviar el catálogo y los archivos
    private void manejarCliente(Socket cliente) {
        try (ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream())) {
            // Leer los datos enviados por el cliente
            Map<Integer, Integer> carrito = (Map<Integer, Integer>) ois.readObject();
            // Procesar los datos (restar las existencias del catálogo)
            procesarCompra(carrito);
            // Enviar respuesta al cliente
            ObjectOutputStream oos = new ObjectOutputStream(cliente.getOutputStream());
            oos.writeObject("Compra realizada con éxito. Las existencias han sido actualizadas.");
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Nuevo método para procesar la compra y restar las existencias del catálogo
    private void procesarCompra(Map<Integer, Integer> carrito) {
        for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
            int productoId = entry.getKey();
            int cantidad = entry.getValue();
            Producto producto = catalogo.obtenerProductoPorId(productoId).orElse(null);
            if (producto != null) {
                int existenciasActuales = producto.getExistencias();
                if (existenciasActuales >= cantidad) {
                    producto.setExistencias(existenciasActuales - cantidad); // Restar las existencias
                } else {
                    System.out.println("No hay suficientes existencias para el producto con ID " + productoId);
                    // Aquí puedes manejar la situación de existencias insuficientes según tus requerimientos
                }
            }
        }
    }


    public static void main(String[] args) {
        ServidorCarrito servidor = new ServidorCarrito();
        servidor.iniciar();
    }
}
