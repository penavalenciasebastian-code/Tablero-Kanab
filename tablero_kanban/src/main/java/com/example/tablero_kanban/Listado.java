package com.example.tablero_kanban;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("Listado de tareas")
 
public class Listado extends VerticalLayout{
    public TextField tituloField;
    public TextArea descripArea;
    public HorizontalLayout contenedorTarjetas;

    public VerticalLayout tarjetasPendiente;
    public VerticalLayout tarjetasEnProceso;
    public VerticalLayout tarjetasFinalizadas;
    public Div tarjetaSeleccionada;
    public Listado (){
    
    tituloField = new TextField("Titulo de la tarea");
    descripArea = new TextArea("Descripcion");
    contenedorTarjetas = new HorizontalLayout();
    contenedorTarjetas.setPadding(false);
    VerticalLayout columna1 = crearColumna("Pendiente", tarjetasPendiente = new VerticalLayout());
    VerticalLayout columna2 = crearColumna("En Proceso", tarjetasEnProceso = new VerticalLayout());
    VerticalLayout columna3 = crearColumna("Finalizada", tarjetasFinalizadas = new VerticalLayout());
    HorizontalLayout tableroKanban = new HorizontalLayout(columna1, columna2, columna3);
    tableroKanban.setWidthFull();
    tableroKanban.setSpacing(true);
    Button crearCard = new Button("Añadir Tarjeta", e -> nuevaTarjeta());
    setAlignItems(Alignment.CENTER);

        add(tableroKanban, tituloField, descripArea, contenedorTarjetas, crearCard);
    }
    public void nuevaTarjeta (){
        String titulo = tituloField.getValue();
        String descripcion = descripArea.getValue();
        if (!titulo.trim().isEmpty()) {
            Div nuevaTarea = crearTarjeta(titulo, descripcion);
            contenedorTarjetas.add(nuevaTarea);
            tarjetasPendiente.add(nuevaTarea);

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
        descripcionCard.getStyle().set("Color", "#024d8a");

        card.add(tituloCard,descripcionCard);
        card.addClickListener(e ->{
            if (tarjetaSeleccionada != null) {
                tarjetaSeleccionada.getStyle().set("border", "2px solid transparent");
                
            }
            tarjetaSeleccionada = card;
            tarjetaSeleccionada.getStyle().set("border", "2px solid #007bff");
        });
        return card;
        
        
    }
    public VerticalLayout crearColumna(String tituloColumna, VerticalLayout contenedorTarjetas) {
        VerticalLayout columna = new VerticalLayout();
        columna.setWidth("320px");
        columna.setMinWidth("300px");
        columna.getStyle().set("background-color", "#f4f5f7");
        columna.getStyle().set("border-radius", "8px");
        columna.getStyle().set("padding", "16px");

        H4 titulo = new H4(tituloColumna);
        titulo.getStyle().set("margin-top", "0");

        contenedorTarjetas.setPadding(false);
        contenedorTarjetas.setWidthFull();
        Button btnMoverA = btnmover(contenedorTarjetas);

        columna.add(titulo, contenedorTarjetas, btnMoverA);
        return columna;
    }
    public Button btnmover(VerticalLayout contenedorPasar){
        Button moverAbtn = new Button("Mover tarea aqui", e->{
            if (tarjetaSeleccionada != null) {
                contenedorPasar.add(tarjetaSeleccionada);
                tarjetaSeleccionada.getStyle().set("border", "2px solid transparent");
                tarjetaSeleccionada = null; 
                
            }
        });
        moverAbtn.setWidthFull();
        moverAbtn.getStyle().set("margin-bottom", "10px");
        return moverAbtn;
        
    }
}