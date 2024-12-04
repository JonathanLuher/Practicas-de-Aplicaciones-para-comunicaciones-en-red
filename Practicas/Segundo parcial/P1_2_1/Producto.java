package P1_2_1;

import java.io.Serializable;

public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombre;
    private double precio;
    private int existencias;
    private String imagenRuta;

    // Constructor con validación básica
    public Producto(int id, String nombre, double precio, int existencias, String imagenRuta) {
        if (id < 0) throw new IllegalArgumentException("El ID no puede ser negativo.");
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        if (existencias < 0) throw new IllegalArgumentException("Las existencias no pueden ser negativas.");
        if (imagenRuta == null || imagenRuta.trim().isEmpty()) throw new IllegalArgumentException("La ruta de la imagen no puede estar vacía.");

        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.existencias = existencias;
        this.imagenRuta = imagenRuta;
    }

    // Getters y setters con validación donde sea necesario
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getExistencias() { return existencias; }
    public void setExistencias(int existencias) {
        if (existencias < 0) throw new IllegalArgumentException("Las existencias no pueden ser negativas.");
        this.existencias = existencias;
    }
    public String getImagenRuta() { return imagenRuta; }
    public void setImagenRuta(String imagenRuta) {
        if (imagenRuta == null || imagenRuta.trim().isEmpty()) throw new IllegalArgumentException("La ruta de la imagen no puede estar vacía.");
        this.imagenRuta = imagenRuta;
    }

    @Override
    public String toString() {
        return String.format("Producto{id=%d, nombre='%s', precio=%.2f, existencias=%d, imagenRuta='%s'}",
                             id, nombre, precio, existencias, imagenRuta);
    }
}
