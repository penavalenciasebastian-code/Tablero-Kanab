package com.example.tablero_kanban;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout{
    public MainView(){
        H1 titulo = new H1("Tablero Kanban");
        setAlignItems(Alignment.CENTER);
        Button listado = new Button("Ir al listado",e ->{
            getUI().ifPresent(ui -> ui.navigate("Listado de tareas"));
        });
        add(titulo,listado);
        
    }
}
