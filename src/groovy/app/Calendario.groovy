package app

import com.vaadin.annotations.Theme
import com.vaadin.data.*
import com.vaadin.data.fieldgroup.FieldGroup
import com.vaadin.data.util.BeanItem
import com.vaadin.data.util.BeanItemContainer
import com.vaadin.grails.Grails
import com.vaadin.navigator.*
import com.vaadin.shared.ui.*
import com.vaadin.shared.ui.datefield.Resolution
import com.vaadin.ui.*
import com.vaadin.ui.components.calendar.CalendarComponentEvents
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.WeekClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.WeekClickHandler
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventMoveHandler
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventResizeHandler
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.handler.BasicDateClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.DateClickEvent;
import com.vaadin.ui.components.calendar.handler.BasicWeekClickHandler
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.themes.ValoTheme
import dominio.Evento
import servicios.EventosService

import java.text.DateFormatSymbols;

/**
 * Created by cristian on 06/11/2020.
 */
@Theme("valo")
class Calendario extends GridLayout implements View{

    private enum Modo {
        MES, SEMANA, DIA;
    }
    private GregorianCalendar calendar;

    /** Target calendar component that this test application is made for. */
    private Calendar componenteCalendario = new Calendar();

    private Date primeraFechaMesActual;

    private final Label mesAnoCalendarioLabel = new Label("");

    private Button botonMes;

    private Button botonSemana;

    private Button botonDia;

    private Button botonSiguiente;

    private Button botonAnterior;

    private TextField campoTitulo;

    private Window ventanaProgramacionEvento;

    private final FormLayout layoutCamposProgramacionEvento = new FormLayout();
    private FieldGroup grupoCamposProgramacionEvento = new FieldGroup();

    private Button botonEliminarEvento;

    private Button botonAplicarEvento;

    private Modo modoVista = Modo.MES;

    private Button agregarEvento;

    private Integer primeraHora;

    private Integer ultimaHora;

    private Integer primerDia;

    private Integer ultimoDia;

    private Locale localizacion = new Locale("es","DO");

    private Collection<Evento> eventos  = new ArrayList<>()
    EventosService eventosService = Grails.get(EventosService)

    private boolean useSecondResolution;

    private DateField campoInicioFecha;
    private DateField campoFinFecha;
    final BeanItemContainer<Evento> container =
            new BeanItemContainer<Evento>(Evento.class);

    public Calendario() {
        setSizeFull();
        setHeight("1000px");
        setMargin(true);
        setSpacing(true);
        setLocale(localizacion);
        inicializarCalendario();
        inicializarContenidoLayout();
    }
    private void inicializarCalendario() {
        componenteCalendario.setTimeZone(TimeZone.default);
        //container.addBean(new Evento(caption: 'Evento',startDate: new Date(),  endDate: new Date(), styleName: "blue"))
        container.addAll(Evento.list())
        componenteCalendario = new Calendar();
        componenteCalendario.setLocale(localizacion);
        componenteCalendario.setImmediate(true);

        componenteCalendario.setSizeFull();


        if (primeraHora != null && ultimaHora != null) {
            componenteCalendario.setFirstVisibleHourOfDay(primeraHora);
            componenteCalendario.setLastVisibleHourOfDay(ultimaHora);
        }

        if (primerDia != null && ultimoDia != null) {
            componenteCalendario.setFirstVisibleDayOfWeek(primerDia);
            componenteCalendario.setLastVisibleDayOfWeek(ultimoDia);
        }

        Date hoy = obtenerFechaHoy();
        calendar = new GregorianCalendar(localizacion);
        calendar.setTime(hoy);
        componenteCalendario.getInternalCalendar().setTime(hoy);

        componenteCalendario.setContainerDataSource(container, "caption",
                "description", "start", "end", "styleName");
        componenteCalendario.setStartDate(componenteCalendario.getStartDate());
        componenteCalendario.setEndDate(componenteCalendario.getEndDate());
        int rollAmount = calendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
        calendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
        primeraFechaMesActual = calendar.getTime();

        actualizarLabelTitulo();
        addCalendarEventListeners();
    }
    private Date obtenerFechaHoy() {
        return new Date();
    }
    private void actualizarLabelTitulo() {
        DateFormatSymbols s = new DateFormatSymbols(localizacion);
        String month = s.getShortMonths()[calendar.get(GregorianCalendar.MONTH)];
        mesAnoCalendarioLabel.setValue(month + " "
                + calendar.get(GregorianCalendar.YEAR));
    }
    private void inicializarContenidoLayout() {
        inicializarBotonesNav();
        inicializarBotonAgregar();

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");
        hl.setSpacing(true);
        hl.addComponent(botonAnterior);
        hl.addComponent(mesAnoCalendarioLabel);

        CssLayout group = new CssLayout();
        group.addStyleName("v-component-group");
        group.addComponent(botonDia);
        group.addComponent(botonSemana);
        group.addComponent(botonMes);
        hl.addComponent(group);

        hl.addComponent(botonSiguiente);
        hl.setComponentAlignment(botonAnterior, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(mesAnoCalendarioLabel, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(group, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(botonSiguiente, Alignment.MIDDLE_RIGHT);

        HorizontalLayout controlPanel = new HorizontalLayout();
        controlPanel.setSpacing(true);
        controlPanel.setWidth("100%");
        controlPanel.setMargin(new MarginInfo(false, false, true, false));
        controlPanel.addComponent(agregarEvento);
       controlPanel.setExpandRatio(agregarEvento, 1.0f);
       controlPanel.setComponentAlignment(agregarEvento, Alignment.BOTTOM_RIGHT);

        Label viewCaption = new Label("Calendario. Practica 10.");
        viewCaption.addStyleName(ValoTheme.LABEL_H1);
        viewCaption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        addComponent(viewCaption);
        addComponent(controlPanel);
        addComponent(hl);
        addComponent(componenteCalendario);
        setRowExpandRatio(getRows() - 1, 1.0f);
    }
    private void inicializarBotonesNav() {
        botonMes = new Button("Mes", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                cambiarAVistaMes();
            }
        });

        botonSemana = new Button("Semana", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                WeekClickHandler handler = (WeekClickHandler) componenteCalendario
                        .getHandler(WeekClick.EVENT_ID);
                handler.weekClick(new WeekClick(componenteCalendario, calendar
                        .get(GregorianCalendar.WEEK_OF_YEAR), calendar
                        .get(GregorianCalendar.YEAR)));
            }
        });

        botonDia = new Button("Dia", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                // simulate day click
                BasicDateClickHandler handler = (BasicDateClickHandler) componenteCalendario
                        .getHandler(DateClickEvent.EVENT_ID);
                handler.dateClick(new CalendarComponentEvents.DateClickEvent(componenteCalendario,
                        new Date()));
            }
        });

        botonSiguiente = new Button("Siguiente", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                handleClickBotonSiguiente();
            }
        });
        botonSiguiente.setStyleName("friendly")

        botonAnterior = new Button("Anterior", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                handleClickBotonAnterior();
            }
        });
        botonAnterior.setStyleName("friendly")
    }
    public void cambiarAVistaMes() {
        modoVista = Modo.MES;
        botonMes.setVisible(false)
        botonDia.setVisible(true)
        botonSemana.setVisible(true)
        int rollAmount = calendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
        calendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);

        componenteCalendario.setStartDate(calendar.getTime());

        actualizarLabelTitulo();

        calendar.add(GregorianCalendar.MONTH, 1);
        calendar.add(GregorianCalendar.DATE, -1);

        componenteCalendario.setEndDate(calendar.getTime());

        calendar.setTime(obtenerFechaHoy());
        // resetearTiempoCalendario(true);
    }
    private void handleClickBotonSiguiente() {
        switch (modoVista) {
            case Modo.MES:
                mesSigte();
                break;
            case Modo.SEMANA:
                semanaSigte();
                break;
            case Modo.DIA:
                diaSigte();
                break;
        }
    }
    private void handleClickBotonAnterior() {
        switch (modoVista) {
            case Modo.MES:
                mesAnterior();
                break;
            case Modo.SEMANA:
                semanaAnterior();
                break;
            case Modo.DIA:
                diaAnterior();
                break;
        }
    }
    private void mesSigte() {
        rodarMes(1);
    }

    private void mesAnterior() {
        rodarMes(-1);
    }

    private void semanaSigte() {
        rodarSemana(1);
    }

    private void semanaAnterior() {
        rodarSemana(-1);
    }

    private void diaSigte() {
        rodarFecha(1);
    }

    private void diaAnterior() {
        rodarFecha(-1);
    }
    private void rodarMes(int direction) {
        calendar.setTime(primeraFechaMesActual);
        calendar.add(GregorianCalendar.MONTH, direction);
        resetearTiempo(false);
        primeraFechaMesActual = calendar.getTime();
        componenteCalendario.setStartDate(primeraFechaMesActual);

        actualizarLabelTitulo();

        calendar.add(GregorianCalendar.MONTH, 1);
        calendar.add(GregorianCalendar.DATE, -1);
        resetearTiempoCalendario(true);
    }

    private void rodarSemana(int direction) {
        calendar.add(GregorianCalendar.WEEK_OF_YEAR, direction);
        calendar.set(GregorianCalendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek());
        resetearTiempoCalendario(false);
        resetearTiempo(true);
        calendar.add(GregorianCalendar.DATE, 6);
        componenteCalendario.setEndDate(calendar.getTime());
    }
    private void rodarFecha(int direction) {
        calendar.add(GregorianCalendar.DATE, direction);
        resetearTiempoCalendario(false);
        resetearTiempoCalendario(true);
    }
    private void resetearTiempo(boolean max) {
        if (max) {
            calendar.set(GregorianCalendar.HOUR_OF_DAY,
                    calendar.getMaximum(GregorianCalendar.HOUR_OF_DAY));
            calendar.set(GregorianCalendar.MINUTE,
                    calendar.getMaximum(GregorianCalendar.MINUTE));
            calendar.set(GregorianCalendar.SECOND,
                    calendar.getMaximum(GregorianCalendar.SECOND));
            calendar.set(GregorianCalendar.MILLISECOND,
                    calendar.getMaximum(GregorianCalendar.MILLISECOND));
        } else {
            calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
            calendar.set(GregorianCalendar.MINUTE, 0);
            calendar.set(GregorianCalendar.SECOND, 0);
            calendar.set(GregorianCalendar.MILLISECOND, 0);
        }
    }
    private void resetearTiempoCalendario(boolean resetEndTime) {
        resetearTiempo(resetEndTime);
        if (resetEndTime) {
            componenteCalendario.setEndDate(calendar.getTime());
        } else {
            componenteCalendario.setStartDate(calendar.getTime());
            actualizarLabelTitulo();
        }
    }
    @SuppressWarnings("serial")
    private void addCalendarEventListeners() {
        // Register week clicks by changing the schedules start and end dates.
        componenteCalendario.setHandler(new BasicWeekClickHandler() {

            @Override
            public void weekClick(WeekClick event) {
                // let BasicWeekClickHandler handle calendar dates, and update
                // only the other parts of UI here
                super.weekClick(event);
                actualizarLabelTitulo();
                switchToWeekView();
            }
        });

        componenteCalendario.setHandler(new EventClickHandler() {

            @Override
            public void eventClick(EventClick event) {
                showEventPopup(event.getCalendarEvent(), false);
                //println(event.calendarEvent.start)
                //componenteCalendario.removeEvent(event.calendarEvent);
            }
        });

        componenteCalendario.setHandler(new BasicDateClickHandler() {

            @Override
            public void dateClick(DateClickEvent event) {
                // let BasicDateClickHandler handle calendar dates, and update
                // only the other parts of UI here
                super.dateClick(event);
                switchToDayView();
            }
        });

        componenteCalendario.setHandler(new RangeSelectHandler() {

            @Override
            public void rangeSelect(RangeSelectEvent event) {
                handleRangeSelect(event);
            }
        });
        componenteCalendario.setHandler( new EventMoveHandler() {
            @Override
            void eventMove(CalendarComponentEvents.MoveEvent event) {
                eventosService.restaurarEventos(container.getItemIds())
            }
        });
        componenteCalendario.setHandler(new EventResizeHandler() {
            @Override
            void eventResize(CalendarComponentEvents.EventResize event) {
                eventosService.restaurarEventos(container.getItemIds())
            }
        })
    }
    public void switchToWeekView() {
        modoVista = Modo.SEMANA;
        botonMes.setVisible(true);
        botonSemana.setVisible(false);
        botonDia.setVisible(true)
    }
    public void switchToDayView() {
        modoVista = Modo.DIA;
        botonMes.setVisible(true);
        botonSemana.setVisible(true);
        botonDia.setVisible(false)
    }
    private void handleRangeSelect(RangeSelectEvent event) {
        Date start = event.getStart();
        Date end = event.getEnd();

        /*
         * If a range of dates is selected in monthly mode, we want it to end at
         * the end of the last day.
         */
        if (event.isMonthlyMode()) {
            end = getEndOfDay(calendar, end);
        }

        showEventPopup(createNewEvent(start, end), true);
    }
    private static Date getEndOfDay(java.util.Calendar calendar, Date date) {
        java.util.Calendar calendarClone = (java.util.Calendar) calendar
                .clone();

        calendarClone.setTime(date);
        calendarClone.set(java.util.Calendar.MILLISECOND,
                calendarClone.getActualMaximum(java.util.Calendar.MILLISECOND));
        calendarClone.set(java.util.Calendar.SECOND,
                calendarClone.getActualMaximum(java.util.Calendar.SECOND));
        calendarClone.set(java.util.Calendar.MINUTE,
                calendarClone.getActualMaximum(java.util.Calendar.MINUTE));
        calendarClone.set(java.util.Calendar.HOUR,
                calendarClone.getActualMaximum(java.util.Calendar.HOUR));
        calendarClone.set(java.util.Calendar.HOUR_OF_DAY,
                calendarClone.getActualMaximum(java.util.Calendar.HOUR_OF_DAY));

        return calendarClone.getTime();
    }
    private void showEventPopup(Evento event, boolean newEvent) {
        if (event == null) {
            return;
        }

        updateCalendarEventPopup(newEvent);
        actualizarFormEventoCalendario(event);
        // TODO this only works the first time
        campoTitulo.focus();

        if (!getUI().getWindows().contains(ventanaProgramacionEvento)) {
            getUI().addWindow(ventanaProgramacionEvento);
        }

    }
    private void updateCalendarEventPopup(boolean newEvent) {
        if (ventanaProgramacionEvento == null) {
            createCalendarEventPopup();
        }

        if (newEvent) {
            ventanaProgramacionEvento.setCaption("Nuevo evento");
        } else {
            ventanaProgramacionEvento.setCaption("Editar evento");
        }

        botonEliminarEvento.setVisible(!newEvent);
        botonEliminarEvento.setEnabled(!componenteCalendario.isReadOnly());
        botonAplicarEvento.setEnabled(!componenteCalendario.isReadOnly());
    }
    private void createCalendarEventPopup() {
        VerticalLayout layout = new VerticalLayout();
        // layout.setMargin(true);
        layout.setSpacing(true);

        ventanaProgramacionEvento = new Window(null, layout);
        ventanaProgramacionEvento.setWidth("400px");
        ventanaProgramacionEvento.setModal(true);
        ventanaProgramacionEvento.center();

        layoutCamposProgramacionEvento.addStyleName("light");
        layoutCamposProgramacionEvento.setMargin(false);
        layout.addComponent(layoutCamposProgramacionEvento);

        botonAplicarEvento = new Button("Aplicar", new ClickListener() {


            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    commitCalendarEvent();
                } catch (CommitException e) {
                    e.printStackTrace();
                }
            }
        });
        botonAplicarEvento.addStyleName("primary");
        Button cancel = new Button("Cancelar", new ClickListener() {


            @Override
            public void buttonClick(ClickEvent event) {
                discardCalendarEvent();
            }
        });
        botonEliminarEvento = new Button("Eliminar", new ClickListener() {


            @Override
            public void buttonClick(ClickEvent event) {
                deleteCalendarEvent();
            }
        });
        botonEliminarEvento.addStyleName("borderless");
        botonEliminarEvento.setStyleName("danger")
        ventanaProgramacionEvento.addCloseListener(new Window.CloseListener() {


            @Override
            public void windowClose(Window.CloseEvent e) {
                discardCalendarEvent();
            }
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addStyleName("v-window-bottom-toolbar");
        buttons.setWidth("100%");
        buttons.setSpacing(true);
        buttons.addComponent(botonEliminarEvento);
        buttons.addComponent(botonAplicarEvento);
        buttons.setExpandRatio(botonAplicarEvento, 1);
        buttons.setComponentAlignment(botonAplicarEvento, Alignment.TOP_RIGHT);
        buttons.addComponent(cancel);
        layout.addComponent(buttons);

    }
    private void discardCalendarEvent() {
        grupoCamposProgramacionEvento.discard();
        getUI().removeWindow(ventanaProgramacionEvento);
    }
    private void deleteCalendarEvent() {
        Evento event = getFormCalendarEvent();
        if (container.containsId(event)) {
            componenteCalendario.removeEvent(event);
            eventosService.restaurarEventos(container.getItemIds())
        }
        getUI().removeWindow(ventanaProgramacionEvento);
    }
    @SuppressWarnings("unchecked")
    private Evento getFormCalendarEvent() {
        BeanItem<CalendarEvent> item = (BeanItem<CalendarEvent>) grupoCamposProgramacionEvento
                .getItemDataSource();
        Evento event = item.getBean();
        return (Evento) event;
    }
    private void commitCalendarEvent() throws CommitException {
        grupoCamposProgramacionEvento.commit();
        Evento event = getFormCalendarEvent();
        if (event.getEnd() == null) {
            event.setEnd(event.getStart());
        }
        if(!container.containsId(event))
        {
            event.haSidoEnviado = false;
            container.addItem(event)
        }
        eventosService.restaurarEventos(container.getItemIds())
        getUI().removeWindow(ventanaProgramacionEvento);
        componenteCalendario.requestRepaintAll()
    }
    private void actualizarFormEventoCalendario(Evento event) {
        BeanItem<Evento> item = new BeanItem<Evento>(event);
        layoutCamposProgramacionEvento.removeAllComponents();
        grupoCamposProgramacionEvento = new FieldGroup();
        initFormFields(layoutCamposProgramacionEvento, Evento.getClass());
        grupoCamposProgramacionEvento.setBuffered(true);
        grupoCamposProgramacionEvento.setItemDataSource(item);
    }
    private void initFormFields(Layout formLayout,
                                Class<? extends CalendarEvent> eventClass) {

        campoInicioFecha = createDateField("Fecha inicio");
        campoFinFecha = createDateField("Fecha fin");

        final CheckBox campoTodoElDia = createCheckBox("Todo el dia");
        campoTodoElDia.addValueChangeListener(new Property.ValueChangeListener() {


            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Object value = event.getProperty().getValue();
                if (value instanceof Boolean && Boolean.TRUE.equals(value)) {
                    setFormDateResolution(Resolution.DAY);

                } else {
                    setFormDateResolution(Resolution.MINUTE);
                }
            }

        });

        campoTitulo = createTextField("Nombre de evento");
        campoTitulo.setInputPrompt("Event name");
        campoTitulo.setRequired(true);
        final TextArea descriptionField = createTextArea("Descripcion");
        descriptionField.setInputPrompt("Describe el evento");
        descriptionField.setRows(3);
        // descriptionField.setRequired(true);

        final ComboBox styleNameField = createStyleNameComboBox();
        styleNameField.setInputPrompt("Choose calendar");
        styleNameField.setTextInputAllowed(false);

        final TextField campoMinutosAntes = createTextField("Minutos de preaviso")

        formLayout.addComponent(campoInicioFecha);
        // campoInicioFecha.setRequired(true);
        formLayout.addComponent(campoFinFecha);
        formLayout.addComponent(campoTodoElDia);
        formLayout.addComponent(campoTitulo);
        // campoTitulo.setComponentError(new UserError("Testing error"));
        formLayout.addComponent(descriptionField);
        formLayout.addComponent(styleNameField);
        formLayout.addComponent(campoMinutosAntes);

        grupoCamposProgramacionEvento.bind(campoInicioFecha, "start");
        grupoCamposProgramacionEvento.bind(campoFinFecha, "end");
        grupoCamposProgramacionEvento.bind(campoTitulo, "caption");
        grupoCamposProgramacionEvento.bind(descriptionField, "description");
        grupoCamposProgramacionEvento.bind(styleNameField, "styleName");
        grupoCamposProgramacionEvento.bind(campoTodoElDia, "allDay");
        grupoCamposProgramacionEvento.bind(campoMinutosAntes, "minutosAntes");
    }
    private TextField createTextField(String caption) {
        TextField f = new TextField(caption);
        f.setNullRepresentation("");
        return f;
    }
    private ComboBox createStyleNameComboBox() {
        ComboBox s = new ComboBox("Calendario");
        s.addContainerProperty("c", String.class, "");
        s.setItemCaptionPropertyId("c");
        Item i = s.addItem("color1");
        i.getItemProperty("c").setValue("Work");
        i = s.addItem("color2");
        i.getItemProperty("c").setValue("Personal");
        i = s.addItem("color3");
        i.getItemProperty("c").setValue("Family");
        i = s.addItem("color4");
        i.getItemProperty("c").setValue("Hobbies");
        return s;
    }
    private TextArea createTextArea(String caption) {
        TextArea f = new TextArea(caption);
        f.setNullRepresentation("");
        return f;
    }
    private void setFormDateResolution(Resolution resolution) {
        if (campoInicioFecha != null && campoFinFecha != null) {
            campoInicioFecha.setResolution(resolution);
            campoFinFecha.setResolution(resolution);
        }
    }
    private CheckBox createCheckBox(String caption) {
        CheckBox cb = new CheckBox(caption);
        cb.setImmediate(true);
        return cb;
    }
    private DateField createDateField(String caption) {
        DateField f = new DateField(caption);
        if (useSecondResolution) {
            f.setResolution(Resolution.SECOND);
        } else {
            f.setResolution(Resolution.MINUTE);
        }
        return f;
    }
    private Evento createNewEvent(Date startDate, Date endDate) {
        Evento event = new Evento();
        event.setCaption("");
        event.setStart(startDate);
        event.setEnd(endDate);
        event.setStyleName("color1");
        return event;
    }
    public void inicializarBotonAgregar() {
        agregarEvento = new Button("Agregar nuevo evento");
        agregarEvento.addStyleName("primary");
        agregarEvento.addStyleName("small");
        agregarEvento.addClickListener(new Button.ClickListener() {


            @Override
            public void buttonClick(ClickEvent event) {
                Date start = obtenerFechaHoy();
                start.setHours(0);
                start.setMinutes(0);
                start.setSeconds(0);

                Date end = getEndOfDay(calendar, start);

                showEventPopup(createNewEvent(start, end), true);
            }
        });
    }
    @Override
    void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}