package com.app.webia.service.impl;

import com.app.webia.entity.Categoria;
import com.app.webia.entity.Producto;
import com.app.webia.entity.User;
import com.app.webia.enums.EstadoProducto;
import com.app.webia.enums.Rol;
import com.app.webia.repository.CategoriaRepository;
import com.app.webia.repository.ProductoRepository;
import com.app.webia.repository.UserRepository;
import com.app.webia.util.SlugUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:admin@demo.com}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:Admin@1234}")
    private String adminPassword;

    @Value("${ADMIN_NOMBRE:Admin}")
    private String adminNombre;

    @Value("${ADMIN_APELLIDO:Demo}")
    private String adminApellido;

    @Value("${DEMO_DATA:true}")
    private boolean demoData;

    public DataLoader(UserRepository userRepository, CategoriaRepository categoriaRepository, ProductoRepository productoRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0 && adminEmail != null) {
            User admin = new User();
            admin.setNombre(adminNombre);
            admin.setApellido(adminApellido);
            admin.setUsername("admin");
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRol(Rol.ROLE_ADMIN);
            admin.setActivo(true);
            userRepository.save(admin);
        }

        List<Categoria> categorias = categoriaRepository.findAll();
        if (categorias.isEmpty()) {
            categoriaRepository.saveAll(List.of(
                    crearCategoria("Electrónica", "electronica"),
                    crearCategoria("Moda", "moda"),
                    crearCategoria("Hogar", "hogar"),
                    crearCategoria("Deportes", "deportes"),
                    crearCategoria("Tecnología", "tecnologia")
            ));
            categorias = categoriaRepository.findAll();
        }

        if (demoData && productoRepository.count() == 0 && !categorias.isEmpty() && userRepository.count() > 0) {
            User vendedor = userRepository.findAll().get(0);
            Categoria catTec = categorias.stream().filter(c -> c.getSlug().equals("tecnologia")).findFirst().orElse(categorias.get(0));
            Categoria catEle = categorias.stream().filter(c -> c.getSlug().equals("electronica")).findFirst().orElse(categorias.get(0));
            Categoria catHogar = categorias.stream().filter(c -> c.getSlug().equals("hogar")).findFirst().orElse(categorias.get(0));
            Categoria catDep = categorias.stream().filter(c -> c.getSlug().equals("deportes")).findFirst().orElse(categorias.get(0));
            Categoria catModa = categorias.stream().filter(c -> c.getSlug().equals("moda")).findFirst().orElse(categorias.get(0));

            productoRepository.saveAll(List.of(
                    crearProducto("Auriculares Wireless Pro", "Sonido premium, cancelación de ruido y batería de larga duración.", new BigDecimal("149.99"), 50, true, catTec, vendedor, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=1200&auto=format&fit=crop"),
                    crearProducto("Teclado Mecánico RGB", "Switches táctiles, retroiluminación RGB y construcción resistente.", new BigDecimal("99.90"), 40, true, catEle, vendedor, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?q=80&w=1200&auto=format&fit=crop"),
                    crearProducto("Lámpara de Escritorio LED", "Iluminación ajustable, diseño minimalista y ahorro energético.", new BigDecimal("59.50"), 80, true, catHogar, vendedor, "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?q=80&w=1200&auto=format&fit=crop"),
                    crearProducto("Botella Térmica Deportiva", "Mantiene temperatura 24h, acero inoxidable y a prueba de fugas.", new BigDecimal("39.99"), 120, true, catDep, vendedor, "https://images.unsplash.com/photo-1526401485004-46910ecc8e51?q=80&w=1200&auto=format&fit=crop"),
                    crearProducto("Camiseta Deportiva Premium", "Tejido transpirable, secado rápido y corte ergonómico.", new BigDecimal("34.90"), 200, true, catModa, vendedor, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?q=80&w=1200&auto=format&fit=crop"),
                    crearProducto("Monitor 24\" Full HD", "Pantalla nítida, 75Hz, ideal para trabajo y gaming.", new BigDecimal("199.00"), 30, true, catTec, vendedor, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?q=80&w=1200&auto=format&fit=crop"),
                    crearProducto("Silla Ergonómica Oficina", "Soporte lumbar ajustable, cómoda para jornadas largas.", new BigDecimal("249.99"), 20, true, catHogar, vendedor, "https://images.unsplash.com/photo-1505843513577-22bb7d21e455?q=80&w=1200&auto=format&fit=crop"),
                    crearProducto("Mochila Técnica 20L", "Resistente al agua, compartimentos y perfecta para día a día.", new BigDecimal("69.90"), 60, true, catDep, vendedor, "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?q=80&w=1200&auto=format&fit=crop")
            ));
        }
    }

    private Categoria crearCategoria(String nombre, String slug) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setSlug(slug);
        c.setActivo(true);
        return c;
    }

    private Producto crearProducto(String nombre, String desc, BigDecimal precio, int stock, boolean destacado, Categoria cat, User vendedor, String img) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setSlug(SlugUtil.slugify(nombre));
        p.setDescripcion(desc);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setDestacado(destacado);
        p.setEstado(EstadoProducto.ACTIVO);
        p.setCategoria(cat);
        p.setVendedor(vendedor);
        p.setImagenPrincipalUrl(img);
        p.setGaleria("[]");
        return p;
    }
}
