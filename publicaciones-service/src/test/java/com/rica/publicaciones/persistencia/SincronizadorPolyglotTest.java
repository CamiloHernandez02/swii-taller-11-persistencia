package com.rica.publicaciones.persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SincronizadorPolyglotTest {

    @Mock
    private PublicacionSqlRepository sqlRepo;

    @Mock
    private PublicacionMongoRepository mongoRepo;

    @Mock
    private CacheRedis cache;

    private SincronizadorPolyglot sincronizador;

    @BeforeEach
    void setUp() {
        sincronizador = new SincronizadorPolyglot(sqlRepo, mongoRepo, cache);
    }

    @Test
    void testGuardarPropagaATodosLosStorages() {
        PublicacionSql pub = new PublicacionSql("pub-001", "inv-001", "Título", "10.1234/test", 2024, null);
        when(sqlRepo.save(any())).thenReturn(pub);

        PublicacionSql resultado = sincronizador.guardar(pub);

        assertNotNull(resultado);
        verify(sqlRepo, times(1)).save(pub);
        verify(mongoRepo, times(1)).save(any());
        verify(cache, times(1)).guardar(pub);
    }

    @Test
    void testObtenerDelCacheConFallback() {
        PublicacionSql pub = new PublicacionSql("pub-001", "inv-001", "Título", "10.1234/test", 2024, null);
        when(cache.obtener("pub-001")).thenReturn(null);
        when(sqlRepo.findById("pub-001")).thenReturn(Optional.of(pub));

        Optional<PublicacionSql> resultado = sincronizador.obtener("pub-001");

        assertTrue(resultado.isPresent());
        verify(cache, times(2)).obtener("pub-001");
        verify(sqlRepo, times(1)).findById("pub-001");
        verify(cache, times(1)).guardar(pub);
    }
}
