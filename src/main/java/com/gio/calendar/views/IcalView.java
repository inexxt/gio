package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.persistance.CalendarEventRepository;
import com.gio.calendar.utils.IcalParser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import net.fortuna.ical4j.data.ParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static com.gio.calendar.utils.IcalParser.parseFile;

@Route(value = "ical", layout = MainView.class)
@RouteAlias(value = "ical", layout = MainView.class)
@PageTitle("Ical files")
@CssImport("./views/overview/overview-view.css")
public class IcalView extends Div{

    private void showOutput(String text,
                            HasComponents outputContainer) {
        Component content = new Paragraph();
        HtmlComponent p = new HtmlComponent(com.vaadin.flow.component.Tag.P);
        p.getElement().setText(text);
        outputContainer.add(p);
        outputContainer.add(content);
    }

    private InputStream inputStream() {
        return IcalParser.exportEvents(CalendarEventRepository.findAll());
    }


    public IcalView() {
        addClassName("ical-view");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        Div output = new Div();

        upload.addSucceededListener(event -> {
            String message ="";
            try {
                List<CalendarEvent> events = parseFile(buffer.getInputStream());
                boolean full = true;
                for (CalendarEvent eventCalendar : events) {
                    Optional<String> err = Optional.empty();
                    err = CalendarEventRepository.save(eventCalendar);
                    if (err.isPresent())
                        full = false;
                }
                if (full)
                    message = "Success";
                else
                    message = "Some events were not added";
            }
            catch (IOException | ParserException e) {
                message = e.getMessage();
            }
            finally {
                output.removeAll();
                showOutput(message, output);
            }
        });

        upload.addFileRejectedListener(event -> {
            output.removeAll();
            showOutput(event.getErrorMessage(), output);
        });
        upload.getElement().addEventListener("file-remove", event -> {
            output.removeAll();
        });


        Anchor anchor =
                new Anchor(
                        new StreamResource( "myics.ics" , this::inputStream) ,
                        ""
                )
        ;
        anchor.getElement().setAttribute( "download" , true );
        Button downloadButton = new Button("Export calendar", new Icon( VaadinIcon.DOWNLOAD_ALT ) );
        anchor.add( downloadButton );
        add(new H3("Upload your ical file"), upload, output, new H3("Click below to export your calendar"), anchor);
    }
}
