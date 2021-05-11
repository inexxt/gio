package com.gio.calendar.views;

import com.gio.calendar.models.CalendarEvent;
import com.gio.calendar.persistance.CalendarEventRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;

import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import net.fortuna.ical4j.data.ParserException;

import java.io.IOException;
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

        add(upload, output);
    }
}
