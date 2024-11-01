/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.Models;

import lombok.Data;


@Data
public class Dtostockdata {
    private String nemotecnico;
    private String ultimoPrecio;
    private String variacionPorcentual;
    private String volumenes;
    private String cantidad;
    private String variacionAbsoluta;
    private String precioApertura;
    private String precioMaximo;
    private String precioMinimo;
    private String precioPromedio;
    private String emisorNombre;
}
