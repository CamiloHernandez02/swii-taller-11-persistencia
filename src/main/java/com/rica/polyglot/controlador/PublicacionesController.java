package com.rica.polyglot.controlador;

import com.rica.polyglot.persistencia.PublicacionSql;
import com.rica.polyglot.persistencia.PublicacionSqlRepository;
import com.rica.polyglot.sincronizacion.SincronizadorPolyglot;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publicaciones")
public class PublicacionesController {
    private final PublicacionSqlRepository repo;
    private final SincronizadorPolyglot sincronizador;

    public PublicacionesController(PublicacionSqlRepository repo, SincronizadorPolyglot sincronizador) {
        this.repo = repo;
        this.sincronizador = sincronizador;
    }

    @PostMapping
    public PublicacionSql crear(@RequestBody PublicacionSql pub) {
        sincronizador.crearPublicacion(pub);
        return pub;
    }

    @GetMapping("/{doi}")
    public PublicacionSql obtener(@PathVariable String doi) {
        return sincronizador.obtenerPublicacion(doi);
    }

    @PostMapping("/sincronizar")
    public String sincronizar() {
        sincronizador.sincronizarDesdePostgres();
        return "Sincronización completada";
    }
}
