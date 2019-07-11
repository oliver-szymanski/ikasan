package org.ikasan.dashboard.ui.administration.component;

import com.github.appreciated.css.grid.GridLayoutComponent;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.layout.FluentGridLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.ikasan.security.model.User;
import org.ikasan.security.service.UserService;


public class UserManagementDialog extends Dialog
{
    private User user;
    private UserService userService;

    public UserManagementDialog(User user, UserService userService)
    {
        this.user = user;
        if(this.user == null)
        {
            throw new IllegalArgumentException("User cannot be null!");
        }
        this.userService = userService;
        if(this.userService == null)
        {
            throw new IllegalArgumentException("User Service cannot be null!");
        }

        init();
    }

    private void init()
    {
        FluentGridLayout layout = new FluentGridLayout()
            .withTemplateRows(new Flex(1), new Flex(1.25), new Flex(1.25))
            .withTemplateColumns(new Flex(1), new Flex(1), new Flex(1))
            .withRowAndColumn(initUserForm(), 1,1, 1, 2)
            .withRowAndColumn(createLastAccessGrid(), 2,3, 3, 3)
            .withRowAndColumn(createRolesAccessGrid(), 2,1,  2, 3)
            .withRowAndColumn(createSecurityChangesGrid(), 3, 3)
            .withRowAndColumn(createLdapGroupGrid(), 3,1, 3, 3)
            .withPadding(true)
            .withSpacing(true)
            .withOverflow(FluentGridLayout.Overflow.AUTO);
        layout.setSizeFull();
        setSizeFull();
        add(layout);
    }

    private VerticalLayout createLastAccessGrid()
    {
        H3 dashboardActivityLabel = new H3("Dashboard Activity");

        Grid<User> grid = new Grid<>();

        grid.setClassName("my-grid");
        grid.addColumn(User::getUsername).setKey("username").setHeader("Username").setSortable(true);

        grid.addColumn(User::getFirstName).setKey("firstname").setHeader("First Name").setSortable(true);
        grid.addColumn(User::getSurname).setKey("surname").setHeader("Surname").setSortable(true);
        grid.addColumn(User::getEmail).setKey("email").setHeader("Email").setSortable(true);
        grid.addColumn(User::getDepartment).setKey("department").setHeader("Department").setSortable(true);
        grid.addColumn(User::getPreviousAccessTimestamp).setHeader("Last Access").setSortable(true);

        grid.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(dashboardActivityLabel, grid);
        return layout;
    }

    private VerticalLayout createRolesAccessGrid()
    {
        H3 rolesLabel = new H3("Ikasan Roles");

        Grid<User> grid = new Grid<>();

        grid.setClassName("my-grid");
        grid.addColumn(User::getUsername).setKey("username").setHeader("Username").setSortable(true);

        grid.addColumn(User::getFirstName).setKey("firstname").setHeader("First Name").setSortable(true);
        grid.addColumn(User::getSurname).setKey("surname").setHeader("Surname").setSortable(true);
        grid.addColumn(User::getEmail).setKey("email").setHeader("Email").setSortable(true);
        grid.addColumn(User::getDepartment).setKey("department").setHeader("Department").setSortable(true);
        grid.addColumn(User::getPreviousAccessTimestamp).setHeader("Last Access").setSortable(true);

        grid.setSizeFull();

        Button addRoleButton = new Button("Add role");

        HorizontalLayout labelLayout = new HorizontalLayout();
        labelLayout.setWidthFull();
        labelLayout.add(rolesLabel);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(addRoleButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, addRoleButton);

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.add(labelLayout, buttonLayout);

        VerticalLayout layout = new VerticalLayout();
        layout.add(headerLayout, grid);
        return layout;
    }

    private VerticalLayout createLdapGroupGrid()
    {
        H3 ldapGroupsLabel = new H3("LDAP Groups");
        Grid<User> grid = new Grid<>();

        grid.setClassName("my-grid");
        grid.addColumn(User::getUsername).setKey("username").setHeader("Username").setSortable(true);

        grid.addColumn(User::getFirstName).setKey("firstname").setHeader("First Name").setSortable(true);
        grid.addColumn(User::getSurname).setKey("surname").setHeader("Surname").setSortable(true);
        grid.addColumn(User::getEmail).setKey("email").setHeader("Email").setSortable(true);
        grid.addColumn(User::getDepartment).setKey("department").setHeader("Department").setSortable(true);
        grid.addColumn(User::getPreviousAccessTimestamp).setHeader("Last Access").setSortable(true);

        grid.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(ldapGroupsLabel, grid);
        return layout;
    }

    private VerticalLayout createSecurityChangesGrid()
    {
        H3 userSecurityChangesLabel = new H3("User Security Changes");
        Grid<User> grid = new Grid<>();

        grid.setClassName("my-grid");
        grid.addColumn(User::getUsername).setKey("username").setHeader("Username").setSortable(true);

        grid.addColumn(User::getFirstName).setKey("firstname").setHeader("First Name").setSortable(true);
        grid.addColumn(User::getSurname).setKey("surname").setHeader("Surname").setSortable(true);
        grid.addColumn(User::getEmail).setKey("email").setHeader("Email").setSortable(true);
        grid.addColumn(User::getDepartment).setKey("department").setHeader("Department").setSortable(true);
        grid.addColumn(User::getPreviousAccessTimestamp).setHeader("Last Access").setSortable(true);

        grid.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(userSecurityChangesLabel, grid);
        return layout;
    }

    private VerticalLayout initUserForm()
    {
        H3 userProfileLabel = new H3("User Profile");

        FormLayout formLayout = new FormLayout();
        TextField usernameTf = new TextField("Username");
        usernameTf.setWidth("400px");
        formLayout.add(usernameTf, new Div());

        TextField firstnameTf = new TextField("First name");
        firstnameTf.setWidth("400px");
        formLayout.add(firstnameTf);

        TextField surnameTf = new TextField("Surname");
        surnameTf.setWidth("400px");
        formLayout.add(surnameTf);

        TextField departmentTf = new TextField("Department");
        departmentTf.setWidth("400px");
        formLayout.add(departmentTf);

        TextField emailTf = new TextField("Email");
        emailTf.setWidth("400px");
        formLayout.add(emailTf);
        formLayout.setSizeFull();

        Div result = new Div();
        result.add(formLayout);
        result.setSizeFull();

        formLayout.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.add(userProfileLabel, formLayout);
        return layout;
    }
}