package P1_2_1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Catalogo implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<Producto> productos;

  public Catalogo() {
    productos = new ArrayList<>();
    cargarProductosInicial();
  }

  // Cargar productos predeterminados
  private void cargarProductosInicial() {
    // Lista de productos precargada

    agregarProducto(new Producto(1, "Shampoo para perros", 300, 30, "img/shampoo_perros.jpeg"));
    agregarProducto(new Producto(2, "Jabón medicado", 250, 25, "img/jabon_medicado.jpeg"));
    agregarProducto(new Producto(3, "Collar antipulgas para gatos", 100, 40, "img/collar_antipulgas_gatos.jpeg"));
    agregarProducto(new Producto(4, "Collar rosa", 100, 50, "img/collar_rosa.jpeg"));        
    agregarProducto(new Producto(5, "Collar azul", 100, 50, "img/collar_azul.jpeg"));
    agregarProducto(new Producto(6, "Pechera ajustable", 150, 35, "img/pechera_ajustable.png"));
    agregarProducto(new Producto(7, "Correa retráctil", 200, 40, "img/correa_retractil.jpeg"));
    agregarProducto(new Producto(8, "Bola de hamster", 150, 30, "img/bola_hamster.jpeg"));
    agregarProducto(new Producto(9, "Jaula para pájaros", 400, 15, "img/jaula_pajaros.jpeg"));
    agregarProducto(new Producto(10, "Rascador para gatos", 150, 20, "img/rascador_gatos.jpeg"));
    agregarProducto(new Producto(11, "Casa para perros", 1000, 10, "img/casa_perros.jpeg"));
    agregarProducto(new Producto(12, "Alimento para gatos premium", 800, 50, "img/alimento_gatos.png"));
    agregarProducto(new Producto(13, "Alimento para perros adultos", 700, 50, "img/alimento_perros.jpeg"));
    agregarProducto(new Producto(14, "Camita para mascotas pequeñas", 500, 30, "img/camita_mascotas.jpeg"));
    agregarProducto(new Producto(15, "Juguete mordedor", 100, 60, "img/juguete_mordedor.jpeg"));
    agregarProducto(new Producto(16, "Comedero automático", 2000, 20, "img/comedero_automatico.jpeg"));    
    agregarProducto(new Producto(17, "Bebedero fuente", 450, 20, "img/bebedero_fuente.jpeg"));
    agregarProducto(new Producto(18, "Cepillo para perros", 80, 40, "img/cepillo_perros.jpeg"));
    agregarProducto(new Producto(19, "Cepillo para gatos", 80, 40, "img/cepillo_gatos.png"));
    agregarProducto(new Producto(20, "Arena para gatos", 70, 50, "img/arena_gatos.jpeg"));
    agregarProducto(new Producto(21, "Piedras sanitarias", 60, 40, "img/piedras_sanitarias.jpeg"));
    agregarProducto(new Producto(22, "Transportadora mediana", 500, 15, "img/transportadora.jpeg"));
    agregarProducto(new Producto(23, "Vitamina para aves", 100, 30, "img/vitamina_aves.jpeg"));
    agregarProducto(new Producto(24, "Antiparasitario", 300, 30, "img/antiparasitario.jpeg"));
    agregarProducto(new Producto(25, "Pelota para perros", 100, 40, "img/pelota_perros.jpeg"));
    agregarProducto(new Producto(26, "Raton para gatos", 150, 40, "img/raton_gatos.jpeg"));
    agregarProducto(new Producto(27, "Snacks para perros", 9.99, 50, "img/snacks_perros.jpeg"));
    agregarProducto(new Producto(28, "Snacks para gatos", 9.99, 50, "img/snacks_gatos.jpeg"));
    agregarProducto(new Producto(29, "Casas para gato", 200, 30, "img/casa_gato.jpeg"));
    agregarProducto(new Producto(30, "Casa para hamsters", 34.99, 15, "img/casa_hamster.jpeg"));
  }

  // Agregar producto al catálogo
  public void agregarProducto(Producto producto) {
    productos.add(producto);
    guardarCatalogo();
  }

  // Eliminar producto por ID
  public void eliminarProducto(int productoId) {
    productos.removeIf(p -> p.getId() == productoId);
    guardarCatalogo();
  }

  // Actualizar existencias de un producto
  public void actualizarExistencias(int productoId, int nuevasExistencias) {
    obtenerProductoPorId(productoId).ifPresent(p -> {
      p.setExistencias(nuevasExistencias);
      guardarCatalogo();
    });
  }

  // Obtener un producto por su ID
  public Optional<Producto> obtenerProductoPorId(int id) {
    return productos.stream()
        .filter(p -> p.getId() == id)
        .findFirst();
  }

  // Guardar el estado actual del catálogo a archivo
  public void guardarCatalogo() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("catalogo.dat"))) {
      oos.writeObject(this);
    } catch (IOException e) {
      System.err.println("Error al guardar el catálogo: " + e.getMessage());
    }
  }

  // Generar nuevo ID
  public int generarNuevoId() {
    if (productos.isEmpty()) {
      return 1; // Empieza numeración desde 1 si la lista está vacía
    } else {
      // Obtiene el ID más alto y le suma 1
      return productos.stream().mapToInt(Producto::getId).max().getAsInt() + 1;
    }
  }

  // Cargar el catálogo desde archivo
  public static Catalogo cargarDesdeArchivo() {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("catalogo.dat"))) {
      return (Catalogo) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("No se pudo cargar el catálogo desde archivo: " + e.getMessage());
      return new Catalogo(); // Retorna un catálogo nuevo si no se encuentra el archivo
    }
  }

  public List<Producto> obtenerProductos() {
    return new ArrayList<>(productos);
  }
}
