package com.example.tablero_kanban;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
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
    public Select<String> prioridad = new Select<>(); 
    public Listado (){
    
    tituloField = new TextField("Titulo de la tarea");
    descripArea = new TextArea("Descripcion");
    prioridad.setLabel("Prioridad");
    prioridad.setItems("Baja", "Media", "Alta");
    prioridad.setValue("Media");
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

        add(tableroKanban, tituloField, descripArea, prioridad, contenedorTarjetas, crearCard);
    }
    public void nuevaTarjeta (){
        String titulo = tituloField.getValue();
        String descripcion = descripArea.getValue();
        String prioridadString = prioridad.getValue();
        if (!titulo.trim().isEmpty()) {
            Div nuevaTarea = crearTarjeta(titulo, descripcion, prioridadString);
            contenedorTarjetas.add(nuevaTarea);
            tarjetasPendiente.add(nuevaTarea);
            ordenarColumna(tarjetasPendiente);
            ordenarColumna(tarjetasEnProceso);
            ordenarColumna(tarjetasFinalizadas);
            tituloField.clear();
            descripArea.clear();
            prioridad.setValue("Media");
        }
        
    }
    public Div crearTarjeta(String titulo, String descripcion, String prioridadString){
        Div card = new Div();
        card.getStyle().set("background-color", "#ffffff");
        card.getStyle().set("border-radius", "8px");
        card.getStyle().set("padding", "12px");
        card.getStyle().set("box-shadow", "0 1px 3px rgba(0,0,0,0.1)");
        card.getStyle().set("margin-bottom", "10px");
        card.getStyle().set("width", "100%");
        card.getStyle().set("cursor", "pointer");
        card.getStyle().set("border", "2px solid transparent");
        card.getElement().setAttribute("data-prioridad", prioridadString.trim());
         
        H4 tituloCard = new H4(titulo);
        tituloCard.getStyle().set("margin", "0 0 8px 0");
        
        Paragraph descripcionCard = new Paragraph(descripcion);
        descripcionCard.getStyle().set("margin", "0");
        descripcionCard.getStyle().set("Color", "#024d8a");

        Paragraph prioridadCard = new Paragraph("Prioridad: " + prioridadString);
        prioridadCard.getStyle().set("margin", "0");
        prioridadCard.getStyle().set("font-size", "0.85rem");
        prioridadCard.getStyle().set("font-weight", "bold");
        
        if (prioridadString.trim().equals("Alta")) {
            prioridadCard.getStyle().set("color", "#d9534f");
        } else if (prioridadString.trim().equals("Media")) {
            prioridadCard.getStyle().set("color", "#f0ad4e");
        } else {
            prioridadCard.getStyle().set("color", "#5cb85c");
        }


        card.add(tituloCard,descripcionCard, prioridadCard);
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
                ordenarColumna(contenedorPasar);
            }
        });
        moverAbtn.setWidthFull();
        moverAbtn.getStyle().set("margin-bottom", "10px");
        return moverAbtn;
        
    }

    public void ordenarColumna(VerticalLayout columna){
        List<Component> tarjetas = columna.getChildren().collect(Collectors.toList());
        tarjetas.sort((t1, t2) ->{
            String p1 = t1.getElement().getAttribute("data-prioridad");
            String p2 = t2.getElement().getAttribute("data-prioridad");
            return Integer.compare(nivelP(p1), nivelP(p2));
        });
        columna.removeAll();
        tarjetas.forEach(columna::add);
    }
    public int nivelP (String prioridad){
        if (prioridad ==null) {
            return 2;
        }else if (prioridad.equalsIgnoreCase("Alta")) {
            return 1;
        }else if (prioridad.equalsIgnoreCase("Media")) {
            return 2;
        }else if (prioridad.equalsIgnoreCase("Baja")) {
            return 3;
        }else{
            return 2;
        }
    }
}