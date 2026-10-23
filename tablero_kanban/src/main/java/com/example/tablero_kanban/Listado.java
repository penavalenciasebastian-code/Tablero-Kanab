package com.example.tablero_kanban;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("Listado de tareas")
 
public class Listado extends VerticalLayout{
    TextField tituloField;
    TextArea descripArea;
     VerticalLayout contenedorTarjetas;
    public Listado (){
    
    tituloField = new TextField("Titulo de la tarea");
    descripArea = new TextArea("Descripcion");
    contenedorTarjetas = new VerticalLayout();
    contenedorTarjetas.setPadding(false);
    Button crearCard = new Button("Añadir Tarjeta", e -> nuevaTarjeta());

        add(tituloField,descripArea,contenedorTarjetas,crearCard);
    }
    public void nuevaTarjeta (){
        String titulo = tituloField.getValue();
        String descripcion = descripArea.getValue();
        if (!titulo.trim().isEmpty()) {
            Div nuevaTarea = crearTarjeta(titulo, descripcion);
            contenedorTarjetas.add(nuevaTarea);

            tituloField.clear();
            descripArea.clear();
        }
    }
    public Div crearTarjeta(String titulo, String descripcion){
        Div card = new Div();
        // Estilos, etc 
        H4 tituloCard = new H4(titulo);
        tituloCard.getStyle().set("margin", "0 0 8px 0");
        
        Paragraph descripcionCard = new Paragraph(descripcion);
        descripcionCard.getStyle().set("margin", "0");
        descripcionCard.getStyle().set("Color", "#666666");

        card.add(tituloCard,descripcionCard);
        return card;
        
        
    }
}