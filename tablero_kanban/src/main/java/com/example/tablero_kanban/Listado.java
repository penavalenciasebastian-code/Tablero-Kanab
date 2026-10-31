package com.example.tablero_kanban;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
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
    public String ruta = "tablero.txt";
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
    Button btnEliminar = new Button("Eliminar Seleccionada", e -> Eliminar());
    btnEliminar.getStyle().set("background-color", "#d9534f");
    btnEliminar.getStyle().set("color", "white");
    
    Button btnEliminarTodo = new Button("Eliminar Todo", e -> EliminarTodo());
    btnEliminarTodo.getStyle().set("background-color", "#c9302c");
    btnEliminarTodo.getStyle().set("color", "white");
    
    HorizontalLayout botones = new HorizontalLayout(crearCard, btnEliminar, btnEliminarTodo);
    
    
    setAlignItems(Alignment.CENTER);

        add(tableroKanban, tituloField, descripArea, prioridad, contenedorTarjetas,botones);
        cargarTareas();
    }
    public void nuevaTarjeta (){
        String titulo = tituloField.getValue();
        String descripcion = descripArea.getValue();
        String prioridadString = prioridad.getValue();
        if (!titulo.trim().isEmpty()) {
            Div nuevaTarea = crearTarjeta(titulo, descripcion, prioridadString, false, "");
            contenedorTarjetas.add(nuevaTarea);
            tarjetasPendiente.add(nuevaTarea);
            ordenarColumna(tarjetasPendiente);
            ordenarColumna(tarjetasEnProceso);
            ordenarColumna(tarjetasFinalizadas);
            tituloField.clear();
            descripArea.clear();
            prioridad.setValue("Media");

            guardarTarea();
        }
        
    }
    public Div crearTarjeta(String titulo, String descripcion, String prioridadString, boolean estado, String subtareas){
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
        card.getElement().setAttribute("data-titulo", titulo);
        card.getElement().setAttribute("data-descripcion", descripcion);
        card.getElement().setAttribute("data-completado", String.valueOf(estado));
        card.getElement().setAttribute("data-subtareas", subtareas != null ? subtareas : ""); 
        
        
        
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
        Checkbox completado = new Checkbox("Marcar como completada");
        completado.setValue(estado);
        if (estado) {
            card.getStyle().set("background-color", "#e2f0d9");
            tituloCard.getStyle().set("text-decoration", "line-through");
        }
        completado.addValueChangeListener(event -> {
            boolean marcado = event.getValue();
            card.getElement().setAttribute("data-completado", String.valueOf(marcado));
            if (marcado) {
                card.getStyle().set("background-color", "#e2f0d9");
                tituloCard.getStyle().set("text-decoration", "line-through");
            } else {
                card.getStyle().set("background-color", "#ffffff");
                tituloCard.getStyle().set("text-decoration", "none");
            }
            guardarTarea(); 
        });  
        VerticalLayout contenedorSubtareas = new VerticalLayout();
        contenedorSubtareas.setPadding(false);
        contenedorSubtareas.setSpacing(false);
        contenedorSubtareas.getStyle().set("margin-bottom", "8px");

        if (subtareas != null && !subtareas.trim().isEmpty()) {
            String[] arregloSubtareas = subtareas.split(";");
            for (String sub : arregloSubtareas) {
                if (sub.contains("=")) {
                    String[] partes = sub.split("=");
                    String textoSub = partes[0];
                    boolean estadoSub = Boolean.parseBoolean(partes[1]);
                    
                    Checkbox subtarea = crearSubtarea(textoSub, estadoSub, card, contenedorSubtareas);
                    contenedorSubtareas.add(subtarea);
                }
            }
        }

        HorizontalLayout layoutInputSubtarea = new HorizontalLayout();
        layoutInputSubtarea.setWidthFull();
        TextField inputNuevaSub = new TextField();
        inputNuevaSub.setPlaceholder("Nueva subtarea...");
        inputNuevaSub.setWidthFull();
        
        Button btnAddSub = new Button("+", e -> {
            String texto = inputNuevaSub.getValue().trim();
            if (!texto.isEmpty() && !texto.contains("=") && !texto.contains(";")) {
                Checkbox subtarea = crearSubtarea(texto, false, card, contenedorSubtareas);
                contenedorSubtareas.add(subtarea);
                actualizarSubtareas(card, contenedorSubtareas);
                guardarTarea();
                inputNuevaSub.clear();
            }
        });
        layoutInputSubtarea.add(inputNuevaSub, btnAddSub);

        card.add(tituloCard,descripcionCard, prioridadCard, completado, contenedorSubtareas, layoutInputSubtarea);
        card.addClickListener(e ->{
            if (tarjetaSeleccionada != null) {
                tarjetaSeleccionada.getStyle().set("border", "2px solid transparent");
                
            }
            tarjetaSeleccionada = card;
            tarjetaSeleccionada.getStyle().set("border", "2px solid #007bff");
        });
        return card;
        
        
    }
    public Checkbox crearSubtarea(String texto, boolean estado, Div card, VerticalLayout contenedor) {
        Checkbox chk = new Checkbox(texto);
        chk.setValue(estado);
        chk.getStyle().set("font-size", "0.85rem");
        chk.addValueChangeListener(e -> {
            actualizarSubtareas(card, contenedor);
            guardarTarea();
        });
        return chk;
    }
    public void actualizarSubtareas(Div card, VerticalLayout contenedor) {
        StringBuilder textoSubtareas = new StringBuilder();
        for (Component c : contenedor.getChildren().collect(Collectors.toList())) {
            if (c instanceof Checkbox) {
                Checkbox chk = (Checkbox) c;
                textoSubtareas.append(chk.getLabel()).append("=").append(chk.getValue()).append(";");
            }
        }
        card.getElement().setAttribute("data-subtareas", textoSubtareas.toString());
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
                guardarTarea();
            }
        });
        moverAbtn.setWidthFull();
        moverAbtn.getStyle().set("margin-bottom", "10px");
        return moverAbtn;
        
    }
    public void Eliminar() {
        
        if (tarjetaSeleccionada == null) {
            return; 
        }

        Dialog confirmacion = new Dialog();
        H4 texto = new H4("¿Estás seguro de eliminar la tarea seleccionada?");
        
        Button btnSi = new Button("Sí, eliminar", e -> {
            
            tarjetaSeleccionada.getParent().ifPresent(padre -> {
                ((VerticalLayout) padre).remove(tarjetaSeleccionada);
            });
            tarjetaSeleccionada = null;
            guardarTarea();
            confirmacion.close();
        });
        Button btnNo = new Button("Cancelar", e -> confirmacion.close());

        confirmacion.add(texto, new HorizontalLayout(btnSi, btnNo));
        confirmacion.open(); 
    }
    public void EliminarTodo() {
        Dialog confirmacion = new Dialog();
        H4 texto = new H4("¿Esta seguro de eliminar todo? Esto no se puede deshacer.");
        
        Button btnSi = new Button("Sí, vaciar todo", e -> {
            tarjetasPendiente.removeAll();
            tarjetasEnProceso.removeAll();
            tarjetasFinalizadas.removeAll();
            
            tarjetaSeleccionada = null;
            guardarTarea();
            confirmacion.close();
        });
        Button btnNo = new Button("Cancelar", e -> confirmacion.close());

        confirmacion.add(texto, new HorizontalLayout(btnSi, btnNo));
        confirmacion.open();
    }

    public void ordenarColumna(VerticalLayout columna){
        List<Component> tarjetas = columna.getChildren().collect(Collectors.toList());
        tarjetas.sort((t1, t2) ->{
            String p1 = t1.getElement().getAttribute("data-prioridad");
            String p2 = t2.getElement().getAttribute("data-prioridad");
            return Integer.compare(nivelP(p1), nivelP(p2));
        });
        columna.removeAll();
        for (Component tarjeta : tarjetas) {
            columna.add(tarjeta);
        }
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
    public void guardarTarea() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta))) {
            escribirTarjeta(tarjetasPendiente, "Pendiente", pw);
            escribirTarjeta(tarjetasEnProceso, "En Proceso", pw);
            escribirTarjeta(tarjetasFinalizadas, "Finalizada", pw);
        } catch (IOException e) {
            // cambiar esto

            e.printStackTrace();
        }
    }
    public void escribirTarjeta(VerticalLayout columna, String nombreColumna, PrintWriter pw) {
    for (Component component : columna.getChildren().collect(Collectors.toList())) {
        String titulo = component.getElement().getAttribute("data-titulo");
        String descripcion = component.getElement().getAttribute("data-descripcion");
        String prioridad = component.getElement().getAttribute("data-prioridad");
        String completado = component.getElement().getAttribute("data-completado");
        String subtareaString = component.getElement().getAttribute("data-subtareas");
        if (subtareaString == null) {
                subtareaString = "";
            }
        pw.println(titulo + "|" + descripcion + "|" + prioridad + "|" + nombreColumna + "|" + completado + "|" + subtareaString);
    }
}

    public  void cargarTareas() {
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            return; 
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] separar = linea.split("\\|"); 
                if (separar.length >= 4) {
                    String titulo = separar[0];
                    String descripcion = separar[1];
                    String prioridad = separar[2];
                    String nombreColumna = separar[3];
                    boolean listo = false;
                    if (separar.length >= 5) {
                        listo = Boolean.parseBoolean(separar[4]);
                    }
                    String subtareas = "";
                    if (separar.length == 6) {
                        subtareas = separar[5];
                    }

                    Div tarjeta = crearTarjeta(titulo, descripcion, prioridad, listo, subtareas);
                    
                    if (nombreColumna.equals("Pendiente")) {
                        tarjetasPendiente.add(tarjeta);
                    } else if (nombreColumna.equals("En Proceso")) {
                        tarjetasEnProceso.add(tarjeta);
                    } else if (nombreColumna.equals("Finalizada")) {
                        tarjetasFinalizadas.add(tarjeta);
                    }
                }
            }
            ordenarColumna(tarjetasPendiente);
            ordenarColumna(tarjetasEnProceso);
            ordenarColumna(tarjetasFinalizadas);
            
        } catch (IOException e) {
            // cambiar esto






            e.printStackTrace();
        } 
    } 
    


}