/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pii_proyecto2_redsocial;

/**
 *
 * @author emyca
 */
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServidor {
    private static final Map<String, ObjectOutputStream> clientes = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket servidor = new ServerSocket();
            servidor.setReuseAddress(true); 
            servidor.bind(new InetSocketAddress(5000));
            System.out.println("Servidor escuchando en puerto 5000");

            while (true) {
                Socket socket = servidor.accept();
                new Thread(() -> manejarCliente(socket)).start();
            }
        } catch (IOException e) {
            System.out.println("Error en servidor: " + e.getMessage());
        }
    }

    private static void manejarCliente(Socket socket) {
        String username = null;
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream  in  = new ObjectInputStream(socket.getInputStream());

            username = (String) in.readObject();
            clientes.put(username, out);
            System.out.println("✅ " + username + " conectado. Total: " + clientes.size());

            while (true) {
                Mensaje mensaje = (Mensaje) in.readObject();
                reenviar(mensaje);
            }

        } catch (EOFException | SocketException e) {
            System.out.println("❌ " + username + " desconectado.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (username != null) clientes.remove(username);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private static void reenviar(Mensaje mensaje) {
        String destino = mensaje.getReceptor().getUser();
        ObjectOutputStream outDestino = clientes.get(destino);
        if (outDestino != null) {
            try {
                outDestino.writeObject(mensaje);
                outDestino.flush();
                System.out.println(mensaje.getEmisor().getUser() + " → " + destino);
            } catch (IOException e) {
                System.out.println("Error al reenviar a " + destino);
            }
        } else {
            System.out.println(destino + " no está conectado, mensaje perdido.");
        }
    }
}
