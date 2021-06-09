package com.gio.calendar.views;

import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.CssImport;

/**
 * The main view is a top-level placeholder for other views.
 */
@PWA(name = "Auto Calendar", shortName = "Auto Calendar", enableInstallPrompt = false)
@JsModule("./styles/shared-styles.js")
@CssImport("./views/main/main-view.css")
public class MainView extends AppLayout {

    private final Tabs menu;
    private H1 viewTitle;
    private Button changeColor;

    /**
     * Constructor of main view. Performs initialisation of view:
     * initialises the view components and adds them to the overview.
     */
    public MainView() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
    }

    /**
     * Creates component representing header content of page
     * @return Component representing header content of page
     */
    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());
        viewTitle = new H1();
        layout.add(viewTitle);
        layout.add(new Avatar());
        return layout;
    }

    /**
     * Creates component on the left of the page
     * @param menu menu which this component should contain
     * @return Component on the left of the page
     */
    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(new Image("images/logo.png", "My App logo"));
        logoLayout.add(new H1("Auto calendar"));
        changeColor = new Button("Change color");
        changeColor.addClickListener(e -> {
            UI.getCurrent().getPage().executeJs("changeColor()");
        });
        layout.add(logoLayout, menu, changeColor);
        return layout;
    }

    /**
     * Creates menu
     * @return menu
     */
    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(createMenuItems());
        return tabs;
    }

    /**
     * Creates menu items
     * @return Array representing menu items
     */
    private Component[] createMenuItems() {
        return new Tab[]{createTab("Today overview", TodayOverview.class), createTab("Calendar overview", CalendarOverview.class),
                createTab("Add event", NewEventView.class), createTab("Add task", NewTaskView.class),
                createTab("Add reminder", NewReminderView.class), createTab("Add note", NewNoteView.class), createTab("Import/Export Ical", IcalView.class)};
    }

    /**
     * Creates one tab of the menu
     * @param text text of the tab
     * @param navigationTarget target on click
     * @return One tab of the menu
     */
    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    /**
     * Performs action after navigation
     */
    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
        viewTitle.setText(getCurrentPageTitle());
    }

    /**
     * Gets tab of the menu for component
     * @param component
     * @return Tab of the menu associated with component
     */
    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    /**
     * Gets current page title
     * @return String representing current page title
     */
    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
