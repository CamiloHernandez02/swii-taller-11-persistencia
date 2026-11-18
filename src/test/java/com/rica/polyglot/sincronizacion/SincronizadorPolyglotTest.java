package com.rica.polyglot.sincronizacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rica.polyglot.persistencia.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SincronizadorPolyglotTest {
    @Mock
    private PublicacionSqlRepository sqlRepo;
    
    @Mock
    private PublicacionMongoRepository mongoRepo;
    
    @Mock
    private CacheRedis cache;
    
    private SincronizadorPolyglot sincronizador;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        sincronizador = new SincronizadorPolyglot(sqlRepo, mongoRepo, cache, new ObjectMapper());
    }

    @Test
    public void testCrearPublicacion() {
        PublicacionSql pub = new PublicacionSql("Test", "Autor", 2025, "10.1234/test");
        pub.setId(1L);
        
        when(sqlRepo.save(any())).thenReturn(pub);
        
        sincronizador.crearPublicacion(pub);
        
        verify(sqlRepo).save(any(PublicacionSql.class));
        verify(mongoRepo).save(any(PublicacionMongo.class));
        verify(cache).set(eq("10.1234/test"), anyString());
    }

    @Test
    public void testObtenerPublicacion() {
        PublicacionSql expected = new PublicacionSql("Test", "Autor", 2025, "10.1234/test");
        when(cache.get("10.1234/test")).thenReturn(null);
        when(sqlRepo.findByDoi("10.1234/test")).thenReturn(expected);
        
        PublicacionSql resultado = sincronizador.obtenerPublicacion("10.1234/test");
        
        assertEquals(expected, resultado);
        verify(sqlRepo).findByDoi("10.1234/test");
    }
}
