package com.mi.proyecto.ganado.ganadoapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mi.proyecto.ganado.ganadoapp.model.Ganadero;
import com.mi.proyecto.ganado.ganadoapp.model.Usuario;
import com.mi.proyecto.ganado.ganadoapp.model.Veterinario;
import com.mi.proyecto.ganado.ganadoapp.repository.GanaderoRepository;
import com.mi.proyecto.ganado.ganadoapp.repository.UsuarioRepository;
import com.mi.proyecto.ganado.ganadoapp.repository.VeterinarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final GanaderoRepository ganaderoRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          GanaderoRepository ganaderoRepository,
                          VeterinarioRepository veterinarioRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.ganaderoRepository = ganaderoRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));


        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("GANADERO");
        }

        return usuarioRepository.save(usuario);
    }


    public Usuario registrarUsuarioGanadero(Usuario u) {
        u.setRol("GANADERO");
        Usuario saved = registrarUsuario(u);


        Ganadero g = new Ganadero();
        g.setId(saved.getId());
        g.setNombre(saved.getNombre());
        g.setEmail(saved.getEmail());
        g.setPassword(saved.getPassword());
        g.setRol("GANADERO");
        g.setMarcaRegistro(saved.getMarcaRegistro());
        g.setLicencia(saved.getLicencia());
        
        try {
            Ganadero savedG = ganaderoRepository.save(g);
            System.out.println("[REGISTRO] Ganadero registrado: id=" + savedG.getId() + ", email=" + savedG.getEmail());
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo guardar ganadero en colección ganaderos: " + e.getMessage());
        }

        return saved;
    }


    public Usuario registrarVeterinario(Usuario u) {
        u.setRol("VETERINARIO");
        Usuario saved = registrarUsuario(u);


        Veterinario v = new Veterinario();
        v.setId(saved.getId());
        v.setNombre(saved.getNombre());
        v.setEmail(saved.getEmail());
        v.setPassword(saved.getPassword());
        v.setRol("VETERINARIO");
        v.setMarcaRegistro(saved.getMarcaRegistro());
        v.setLicencia(saved.getLicencia());
        
        try {
            Veterinario savedV = veterinarioRepository.save(v);
            System.out.println("[REGISTRO] Veterinario registrado: id=" + savedV.getId() + ", email=" + savedV.getEmail());
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo guardar veterinario en colección veterinarios: " + e.getMessage());
        }

        return saved;
    }


    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }


    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }


    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }


    public Optional<Usuario> buscarPorMarca(String marca) {
        if (marca == null || marca.isEmpty()) {
            return Optional.empty();
        }
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .filter(u -> u.getMarcaRegistro() != null 
                        && u.getMarcaRegistro().equalsIgnoreCase(marca)
                        && "GANADERO".equalsIgnoreCase(u.getRol()))
                .findFirst();
    }


    public long migrateGanaderos() {
        List<Usuario> ganaderos = usuarioRepository.findAll().stream()
                .filter(u -> "GANADERO".equalsIgnoreCase(u.getRol()))
                .toList();
        
        long migrados = 0;
        for (Usuario u : ganaderos) {
            if (ganaderoRepository.findById(u.getId()).isEmpty()) {
                Ganadero g = new Ganadero();
                g.setId(u.getId());
                g.setNombre(u.getNombre());
                g.setEmail(u.getEmail());
                g.setPassword(u.getPassword());
                g.setRol(u.getRol());
                ganaderoRepository.save(g);
                migrados++;
            }
        }
        return migrados;
    }


    public long migrateVeterinarios() {
        List<Usuario> veterinarios = usuarioRepository.findAll().stream()
                .filter(u -> "VETERINARIO".equalsIgnoreCase(u.getRol()))
                .toList();
        
        long migrados = 0;
        for (Usuario u : veterinarios) {
            if (veterinarioRepository.findById(u.getId()).isEmpty()) {
                Veterinario v = new Veterinario();
                v.setId(u.getId());
                v.setNombre(u.getNombre());
                v.setEmail(u.getEmail());
                v.setPassword(u.getPassword());
                v.setRol(u.getRol());
                veterinarioRepository.save(v);
                migrados++;
            }
        }
        return migrados;
    }
}

