import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;

public class UniversityAutomationApp extends JFrame {
    private DataStore dataStore;
    private User currentUser;
    private JComboBox<String> studentUserCombo;
    private JComboBox<String> instructorCombo;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UniversityAutomationApp());
    }

    public UniversityAutomationApp() {
        dataStore = new DataStore();
        setTitle("University Automation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 500);
        setLocationRelativeTo(null);
        showLoginPanel();
        setVisible(true);
    }

    // ==================== HELPER METHODS — Method Abstraction & Reusability ====================

    /**
     * Centralized error dialog — avoids repeating JOptionPane boilerplate
     * across every panel (Reusability principle).
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Formats a double score to 1 decimal place using ValidationUtils.
     * Demonstrates Method Abstraction — the caller does not know the format logic.
     */
    private String fmt(double score) {
        return ValidationUtils.formatScore(score);
    }

    // ==================== LOGIN ====================

    public void showLoginPanel() {
        currentUser = null;
        getContentPane().removeAll();

        BufferedImage bgImage;
        try {
            bgImage = ImageIO.read(getClass().getResource("/arelbackground.png"));
        } catch (IOException ex) {
            bgImage = null;
        }
        final BufferedImage finalBg = bgImage;

        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBg != null) {
                    g.drawImage(finalBg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(true);
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("University Automation System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(new Color(33, 37, 41));
        formPanel.add(usernameLabel, gbc);
        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(new Color(33, 37, 41));
        formPanel.add(passwordLabel, gbc);
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.setOpaque(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setBackground(new Color(0, 123, 255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginBtn, gbc);

        ActionListener loginAction = e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User user = dataStore.authenticate(username, password);
            if (user != null) {
                currentUser = user;
                showDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        };
        loginBtn.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);

        backgroundPanel.add(formPanel);
        getContentPane().add(backgroundPanel);
        revalidate();
        repaint();
    }

    // ==================== DASHBOARD ====================

    public void showDashboard() {
        getContentPane().removeAll();

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel(  currentUser.getFullName() );
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> showLoginPanel());
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(logoutBtn);
        topPanel.add(logoutPanel, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane();

        switch (currentUser.getRole()) {
            case "Admin":
                tabbedPane.addTab("Manage Users", createUserPanel());
                tabbedPane.addTab("Manage Students", createStudentsPanel());
                tabbedPane.addTab("Manage Courses", createCoursesPanel());
                tabbedPane.addTab("Manage Classrooms", createClassroomsPanel());
                tabbedPane.addTab("Reports", createReportsPanel());
                break;
            case "Instructor":
                tabbedPane.addTab("My Courses", createInstructorCoursesPanel());
                tabbedPane.addTab("Grade Entry", createGradeEntryPanel());
                break;
            case "Student":
                tabbedPane.addTab("Available Courses", createStudentCoursePanel());
                tabbedPane.addTab("Enroll", createEnrollmentPanel());
                tabbedPane.addTab("My Courses", createMyCoursesPanel());
                tabbedPane.addTab("Transcript", createTranscriptPanel());
                tabbedPane.addChangeListener(e -> {
                    int idx = tabbedPane.getSelectedIndex();
                    switch (idx) {
                        case 0: tabbedPane.setComponentAt(idx, createStudentCoursePanel()); break;
                        case 1: tabbedPane.setComponentAt(idx, createEnrollmentPanel()); break;
                        case 2: tabbedPane.setComponentAt(idx, createMyCoursesPanel()); break;
                        case 3: tabbedPane.setComponentAt(idx, createTranscriptPanel()); break;
                    }
                });
                break;
        }

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // ==================== ADMIN PANELS ====================

    public JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Form
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New User"));

        formPanel.add(new JLabel("Username:"));
        JTextField tfUsername = new JTextField();
        formPanel.add(tfUsername);

        formPanel.add(new JLabel("Password:"));
        JPasswordField tfPassword = new JPasswordField();
        formPanel.add(tfPassword);

        formPanel.add(new JLabel("Role:"));
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"Admin", "Instructor", "Student"});
        formPanel.add(cbRole);

        formPanel.add(new JLabel("Full Name:"));
        JTextField tfFullName = new JTextField();
        formPanel.add(tfFullName);

        formPanel.add(new JLabel("ID:"));
        JTextField tfRefId = new JTextField();
        formPanel.add(tfRefId);

        JButton addBtn = new JButton("Add User");
        formPanel.add(new JLabel());
        formPanel.add(addBtn);

        // Table
        String[] columns = {"Username", "Role", "Full Name", "ID"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        refreshUserTable(model);

        addBtn.addActionListener(e -> {
            String username = tfUsername.getText().trim();
            String password = new String(tfPassword.getPassword());
            String role = (String) cbRole.getSelectedItem();
            String fullName = tfFullName.getText().trim();
            String refId = tfRefId.getText().trim();

            if (!ValidationUtils.allFieldsFilled(username, password, fullName)) {
                showError("Username, password, and full name are required.");
                return;
            }
            if (dataStore.findUser(username) != null) {
                showError("Username already exists.");
                return;
            }
            dataStore.addUser(new User(username, password, role, fullName, refId));
            refreshUserTable(model);
            refreshRoleCombo(studentUserCombo, "Student");
            refreshRoleCombo(instructorCombo, "Instructor");
            tfUsername.setText(""); tfPassword.setText(""); tfFullName.setText(""); tfRefId.setText("");
            JOptionPane.showMessageDialog(this, "User added successfully.");
        });

        JButton deleteBtn = new JButton("Delete Selected User");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showError("Please select a user to delete."); return; }
            String username = (String) model.getValueAt(row, 0);
            if (username.equals(currentUser.getUsername())) {
                showError("You cannot delete your own account.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete user '" + username + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dataStore.removeUser(username);
                refreshUserTable(model);
                refreshRoleCombo(studentUserCombo, "Student");
                refreshRoleCombo(instructorCombo, "Instructor");
            }
        });

        JPanel topWrapper = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(deleteBtn);
        topWrapper.add(formPanel, BorderLayout.CENTER);
        topWrapper.add(btnPanel, BorderLayout.SOUTH);

        panel.add(topWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshUserTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (User u : dataStore.getUsers()) {
            model.addRow(new Object[]{u.getUsername(), u.getRole(), u.getFullName(), u.getId()});
        }
    }

    public JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Student Profile"));

        formPanel.add(new JLabel("Username (Student):"));
        studentUserCombo = new JComboBox<>();
        refreshRoleCombo(studentUserCombo, "Student");
        formPanel.add(studentUserCombo);

        formPanel.add(new JLabel("Full Name (auto):"));
        JLabel lblFullName = new JLabel("-");
        lblFullName.setFont(lblFullName.getFont().deriveFont(Font.ITALIC));
        formPanel.add(lblFullName);

        formPanel.add(new JLabel("Department:"));
        JTextField tfDept = new JTextField();
        formPanel.add(tfDept);

        formPanel.add(new JLabel("Year (1-4):"));
        JTextField tfYear = new JTextField();
        formPanel.add(tfYear);

        formPanel.add(new JLabel());
        JButton addBtn = new JButton("Add Student Profile");
        formPanel.add(addBtn);

        studentUserCombo.addActionListener(e -> {
            String selected = (String) studentUserCombo.getSelectedItem();
            if (selected != null) {
                User u = dataStore.findUser(selected);
                lblFullName.setText(u != null ? u.getFullName() : "-");
            } else {
                lblFullName.setText("-");
            }
        });
        if (studentUserCombo.getItemCount() > 0) {
            User u = dataStore.findUser((String) studentUserCombo.getItemAt(0));
            lblFullName.setText(u != null ? u.getFullName() : "-");
        }

        String[] columns = {"ID", "Full Name", "Department", "Year", "Username"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        refreshStudentTable(model);

        addBtn.addActionListener(e -> {
            String username = (String) studentUserCombo.getSelectedItem();
            String dept = tfDept.getText().trim();
            String yearStr = tfYear.getText().trim();

            if (username == null || !ValidationUtils.allFieldsFilled(dept, yearStr)) {
                showError("All fields are required.");
                return;
            }
            User linkedUser = dataStore.findUser(username);
            String fullName = (linkedUser != null) ? linkedUser.getFullName() : username;

            int year;
            try {
                year = ValidationUtils.parseYear(yearStr);
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
                return;
            }
            for (StudentProfile sp : dataStore.getStudents()) {
                if (sp.getUsername().equals(username)) {
                    showError("This username already has a student profile.");
                    return;
                }
            }
            dataStore.addStudent(new StudentProfile(fullName, dept, year, username));
            refreshStudentTable(model);
            tfDept.setText(""); tfYear.setText("");
            JOptionPane.showMessageDialog(this, "Student profile added successfully.");
        });

        JButton deleteStudentBtn = new JButton("Delete Selected Student");
        deleteStudentBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showError("Please select a student to delete."); return; }
            String fullName  = (String) model.getValueAt(row, 1);
            String username  = (String) model.getValueAt(row, 4);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete student profile for '" + fullName + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dataStore.removeStudent(username);
                refreshStudentTable(model);
            }
        });

        JPanel topWrapper = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(deleteStudentBtn);
        topWrapper.add(formPanel, BorderLayout.CENTER);
        topWrapper.add(btnPanel, BorderLayout.SOUTH);

        panel.add(topWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Generic combo box population — Reusability.
     * Replaces both refreshStudentUserCombo() and refreshInstructorCombo().
     */
    private void refreshRoleCombo(JComboBox<String> combo, String role) {
        if (combo == null) return;
        combo.removeAllItems();
        for (User u : dataStore.getUsers()) {
            if (role.equals(u.getRole())) {
                combo.addItem(u.getUsername());
            }
        }
    }

    private void refreshStudentTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (StudentProfile sp : dataStore.getStudents()) {
            User u = dataStore.findUser(sp.getUsername());
            String id = (u != null) ? u.getId() : "";
            model.addRow(new Object[]{id, sp.getFullName(), sp.getDepartment(), sp.getYear(), sp.getUsername()});
        }
    }

    public JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Course"));

        formPanel.add(new JLabel("Course Code:"));
        JTextField tfCode = new JTextField();
        formPanel.add(tfCode);

        formPanel.add(new JLabel("Course Name:"));
        JTextField tfName = new JTextField();
        formPanel.add(tfName);

        formPanel.add(new JLabel("Credit:"));
        JTextField tfCredit = new JTextField();
        formPanel.add(tfCredit);

        formPanel.add(new JLabel("Quota:"));
        JTextField tfQuota = new JTextField();
        formPanel.add(tfQuota);

        formPanel.add(new JLabel("Instructor:"));
        instructorCombo = new JComboBox<>();
        refreshRoleCombo(instructorCombo, "Instructor");
        formPanel.add(instructorCombo);

        JButton addBtn = new JButton("Add Course");
        formPanel.add(new JLabel());
        formPanel.add(addBtn);

        String[] columns = {"Course Code", "Course Name", "Credit", "Quota", "Instructor"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        refreshCourseTable(model);

        addBtn.addActionListener(e -> {
            String code = tfCode.getText().trim();
            String name = tfName.getText().trim();
            String creditStr = tfCredit.getText().trim();
            String quotaStr = tfQuota.getText().trim();
            String instructor = (String) instructorCombo.getSelectedItem();

            if (!ValidationUtils.allFieldsFilled(code, name, creditStr, quotaStr) || instructor == null) {
                showError("All fields are required.");
                return;
            }
            int credit, quota;
            try {
                credit = ValidationUtils.parsePositiveInt(creditStr);
                quota  = ValidationUtils.parsePositiveInt(quotaStr);
            } catch (IllegalArgumentException ex) {
                showError("Credit and quota must be positive numbers.");
                return;
            }
            if (dataStore.findCourseByCode(code) != null) {
                showError("Course code already exists.");
                return;
            }
            dataStore.addCourse(new Course(code, name, credit, quota, instructor));
            refreshCourseTable(model);
            tfCode.setText(""); tfName.setText(""); tfCredit.setText(""); tfQuota.setText("");
            JOptionPane.showMessageDialog(this, "Course added successfully.");
        });

        JButton deleteCourseBtn = new JButton("Delete Selected Course");
        deleteCourseBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showError("Please select a course to delete."); return; }
            String courseCode = (String) model.getValueAt(row, 0);
            String courseName = (String) model.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete course '" + courseName + "' (" + courseCode + ")?\n"
                + "All related enrollments and grades will also be removed.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                dataStore.removeCourse(courseCode);
                refreshCourseTable(model);
            }
        });

        JPanel topWrapper = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(deleteCourseBtn);
        topWrapper.add(formPanel, BorderLayout.CENTER);
        topWrapper.add(btnPanel, BorderLayout.SOUTH);

        panel.add(topWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }


    private void refreshCourseTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Course c : dataStore.getCourses()) {
            model.addRow(new Object[]{c.getCourseCode(), c.getCourseName(), c.getCredit(), c.getQuota(), c.getInstructorUsername()});
        }
    }

    public JPanel createClassroomsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Classrooms & Course Assignment"));

        String[] columns = {"Room ID", "Room Name", "Capacity", "Assigned Course"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);

        Runnable refreshClassroomTable = () -> {
            model.setRowCount(0);
            for (Classroom cl : dataStore.getClassrooms()) {
                String assignedCourse = "";
                for (Course c : dataStore.getCourses()) {
                    if (cl.getRoomId().equals(c.getClassroomId())) {
                        assignedCourse = c.getCourseCode() + " - " + c.getCourseName();
                        break;
                    }
                }
                model.addRow(new Object[]{cl.getRoomId(), cl.getRoomName(), cl.getCapacity(), assignedCourse});
            }
        };
        refreshClassroomTable.run();

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton assignBtn = new JButton("Assign Course to Selected Classroom");
        assignBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showError("Please select a classroom first."); return; }
            String roomId = (String) model.getValueAt(row, 0);

            JComboBox<String> courseCombo = new JComboBox<>();
            courseCombo.addItem("(None)");
            for (Course c : dataStore.getCourses()) {
                courseCombo.addItem(c.getCourseCode() + " - " + c.getCourseName());
            }

            for (Course c : dataStore.getCourses()) {
                if (roomId.equals(c.getClassroomId())) {
                    for (int i = 1; i < courseCombo.getItemCount(); i++) {
                        if (courseCombo.getItemAt(i).startsWith(c.getCourseCode() + " - ")) {
                            courseCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                    break;
                }
            }

            int result = JOptionPane.showConfirmDialog(this, courseCombo,
                "Assign Course to " + model.getValueAt(row, 1), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String selected = (String) courseCombo.getSelectedItem();

                for (Course c : dataStore.getCourses()) {
                    if (roomId.equals(c.getClassroomId())) {
                        dataStore.assignClassroomToCourse(c.getCourseCode(), "");
                    }
                }

                if (selected != null && !selected.equals("(None)")) {
                    String courseCode = selected.split(" - ")[0];
                    dataStore.assignClassroomToCourse(courseCode, roomId);
                }
                dataStore.saveClassroomAssignments();
                refreshClassroomTable.run();
                JOptionPane.showMessageDialog(this, "Course assigned successfully.");
            }
        });

        JButton removeBtn = new JButton("Remove Assignment");
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showError("Please select a classroom first."); return; }
            String roomId = (String) model.getValueAt(row, 0);
            String assigned = (String) model.getValueAt(row, 3);
            if (assigned.isEmpty()) { showError("This classroom has no assigned course."); return; }

            for (Course c : dataStore.getCourses()) {
                if (roomId.equals(c.getClassroomId())) {
                    dataStore.assignClassroomToCourse(c.getCourseCode(), "");
                    break;
                }
            }
            dataStore.saveClassroomAssignments();
            refreshClassroomTable.run();
            JOptionPane.showMessageDialog(this, "Assignment removed.");
        });

        btnPanel.add(assignBtn);
        btnPanel.add(removeBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    public JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("System Statistics"));

        JLabel lblUsers = new JLabel();
        JLabel lblStudents = new JLabel();
        JLabel lblCourses = new JLabel();
        JLabel lblEnrollments = new JLabel();
        JLabel lblGrades = new JLabel();

        statsPanel.add(lblUsers);
        statsPanel.add(lblStudents);
        statsPanel.add(lblCourses);
        statsPanel.add(lblEnrollments);
        statsPanel.add(lblGrades);

        JButton refreshBtn = new JButton("Refresh");
        statsPanel.add(refreshBtn);

        String[] enrollCols = {"Student Username", "Course Code"};
        DefaultTableModel enrollModel = new DefaultTableModel(enrollCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable enrollTable = new JTable(enrollModel);

        String[] gradeCols = {"Student", "Course", "Midterm", "Final", "Average", "Letter"};
        DefaultTableModel gradeModel = new DefaultTableModel(gradeCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable gradeTable = new JTable(gradeModel);

        Runnable refreshAll = () -> {
            lblUsers.setText("Users: " + dataStore.getUsers().size());
            lblStudents.setText("Students: " + dataStore.getStudents().size());
            lblCourses.setText("Courses: " + dataStore.getCourses().size());
            lblEnrollments.setText("Enrollments: " + dataStore.getEnrollments().size());
            lblGrades.setText("Grades: " + dataStore.getGrades().size());
            enrollModel.setRowCount(0);
            for (Enrollment e : dataStore.getEnrollments()) {
                enrollModel.addRow(new Object[]{e.getStudentUsername(), e.getCourseCode()});
            }
            gradeModel.setRowCount(0);
            for (GradeRecord g : dataStore.getGrades()) {
                gradeModel.addRow(new Object[]{g.getStudentUsername(), g.getCourseCode(),
                        String.format("%.1f", g.getMidterm()), String.format("%.1f", g.getFinalExam()),
                        String.format("%.1f", g.calculateAverage()), g.getLetterGrade()});
            }
        };
        refreshAll.run();
        refreshBtn.addActionListener(e -> refreshAll.run());

        JTabbedPane reportTabs = new JTabbedPane();
        reportTabs.addTab("Enrollments", new JScrollPane(enrollTable));
        reportTabs.addTab("Grades", new JScrollPane(gradeTable));

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(reportTabs, BorderLayout.CENTER);
        return panel;
    }

    // ==================== INSTRUCTOR PANELS ====================

    public JPanel createInstructorCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("My Courses"));

        String[] columns = {"Course Code", "Course Name", "Credit", "Quota", "Enrolled"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable courseTable = new JTable(model);

        String[] studentCols = {"Student Username", "Student Name"};
        DefaultTableModel studentModel = new DefaultTableModel(studentCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable studentTable = new JTable(studentModel);

        List<Course> myCourses = dataStore.getCoursesByInstructor(currentUser.getUsername());
        for (Course c : myCourses) {
            int enrolled = dataStore.countEnrollmentsForCourse(c.getCourseCode());
            model.addRow(new Object[]{c.getCourseCode(), c.getCourseName(), c.getCredit(), c.getQuota(), enrolled});
        }

        courseTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int row = courseTable.getSelectedRow();
                studentModel.setRowCount(0);
                if (row < 0 || row >= myCourses.size()) return;
                String courseCode = myCourses.get(row).getCourseCode();
                for (Enrollment en : dataStore.getEnrollmentsByCourse(courseCode)) {
                    StudentProfile sp = dataStore.findStudentProfileByUsername(en.getStudentUsername());
                    String name = sp != null ? sp.getFullName() : en.getStudentUsername();
                    studentModel.addRow(new Object[]{en.getStudentUsername(), name});
                }
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(courseTable), new JScrollPane(studentTable));
        splitPane.setDividerLocation(200);
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    public JPanel createGradeEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Grade Entry"));

        // Top: course selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Course:"));
        JComboBox<String> courseCombo = new JComboBox<>();
        List<Course> myCourses = dataStore.getCoursesByInstructor(currentUser.getUsername());
        for (Course c : myCourses) {
            courseCombo.addItem(c.getCourseCode() + " - " + c.getCourseName());
        }
        topPanel.add(courseCombo);

        // Table
        String[] columns = {"Student Username", "Student Name", "Midterm", "Final", "Average", "Letter"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);

        // Bottom: grade entry form
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField tfStudent = new JTextField(12);
        tfStudent.setEditable(false);
        JTextField tfMidterm = new JTextField(6);
        JTextField tfFinal = new JTextField(6);
        JButton saveBtn = new JButton("Save Grade");

        formPanel.add(new JLabel("Student:"));
        formPanel.add(tfStudent);
        formPanel.add(new JLabel("Midterm:"));
        formPanel.add(tfMidterm);
        formPanel.add(new JLabel("Final:"));
        formPanel.add(tfFinal);
        formPanel.add(saveBtn);

        Runnable refreshGradeTable = () -> {
            model.setRowCount(0);
            int idx = courseCombo.getSelectedIndex();
            if (idx < 0 || idx >= myCourses.size()) return;
            String courseCode = myCourses.get(idx).getCourseCode();
            for (Enrollment en : dataStore.getEnrollmentsByCourse(courseCode)) {
                StudentProfile sp = dataStore.findStudentProfileByUsername(en.getStudentUsername());
                String name = sp != null ? sp.getFullName() : en.getStudentUsername();
                GradeRecord g = dataStore.findGrade(en.getStudentUsername(), courseCode);
                if (g != null) {
                    model.addRow(new Object[]{en.getStudentUsername(), name,
                            fmt(g.getMidterm()), fmt(g.getFinalExam()),
                            fmt(g.calculateAverage()), g.getLetterGrade()});
                } else {
                    model.addRow(new Object[]{en.getStudentUsername(), name, "-", "-", "-", "-"});
                }
            }
        };
        refreshGradeTable.run();

        courseCombo.addActionListener(e -> {
            refreshGradeTable.run();
            tfStudent.setText(""); tfMidterm.setText(""); tfFinal.setText("");
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int row = table.getSelectedRow();
                if (row < 0) return;
                tfStudent.setText((String) model.getValueAt(row, 0));
                String midVal = (String) model.getValueAt(row, 2);
                String finVal = (String) model.getValueAt(row, 3);
                tfMidterm.setText("-".equals(midVal) ? "" : midVal);
                tfFinal.setText("-".equals(finVal) ? "" : finVal);
            }
        });

        saveBtn.addActionListener(e -> {
            String studentUsername = tfStudent.getText().trim();
            int courseIdx = courseCombo.getSelectedIndex();
            if (ValidationUtils.isNullOrEmpty(studentUsername) || courseIdx < 0) {
                showError("Please select a student from the table.");
                return;
            }
            double midterm, finalExam;
            try {
                midterm   = ValidationUtils.parseScore(tfMidterm.getText());  // ValidationUtils — Reusability
                finalExam = ValidationUtils.parseScore(tfFinal.getText());
            } catch (IllegalArgumentException ex) {
                showError("Midterm and final must be numbers between 0 and 100.");
                return;
            }
            String courseCode = myCourses.get(courseIdx).getCourseCode();
            dataStore.upsertGrade(studentUsername, courseCode, midterm, finalExam);
            refreshGradeTable.run();
            tfStudent.setText(""); tfMidterm.setText(""); tfFinal.setText("");
            JOptionPane.showMessageDialog(this, "Grade saved successfully.");
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ==================== STUDENT PANELS ====================

    public JPanel createStudentCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Available Courses"));

        String[] columns = {"Course Code", "Course Name", "Credit", "AKTS", "Instructor", "Enrolled", "Available"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);

        for (Course c : dataStore.getCourses()) {
            int enrolled = dataStore.countEnrollmentsForCourse(c.getCourseCode());
            model.addRow(new Object[]{c.getCourseCode(), c.getCourseName(), c.getCredit(),
                    c.getQuota(), c.getInstructorUsername(), enrolled, c.getQuota() - enrolled});
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    public JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Course Enrollment"));

        String[] columns = {"Course Code", "Course Name", "Credit", "Instructor", "Available Spots"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);

        Runnable refreshAvailable = () -> {
            model.setRowCount(0);
            List<Course> enrolledCourses = dataStore.getEnrolledCoursesByStudent(currentUser.getUsername());
            for (Course c : dataStore.getCourses()) {
                boolean alreadyEnrolled = enrolledCourses.stream()
                        .anyMatch(ec -> ec.getCourseCode().equals(c.getCourseCode()));
                if (alreadyEnrolled) continue;
                int enrolled = dataStore.countEnrollmentsForCourse(c.getCourseCode());
                int available = c.getQuota() - enrolled;
                if (available > 0) {
                    model.addRow(new Object[]{c.getCourseCode(), c.getCourseName(), c.getCredit(),
                            c.getInstructorUsername(), available});
                }
            }
        };
        refreshAvailable.run();

        JButton enrollBtn = new JButton("Enroll in Selected Course");
        enrollBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a course to enroll.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String courseCode = (String) model.getValueAt(row, 0);
            boolean success = dataStore.studentEnroll(currentUser.getUsername(), courseCode);
            if (success) {
                JOptionPane.showMessageDialog(this, "Enrolled successfully!");
                refreshAvailable.run();
            } else {
                JOptionPane.showMessageDialog(this, "Enrollment failed. Course may be full or you are already enrolled.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel topBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topBtnPanel.add(enrollBtn);

        panel.add(topBtnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    public JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("My Registered Courses"));

        String[] columns = {"Course Code", "Course Name", "Credit", "Instructor"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);

        Runnable refreshMyCourses = () -> {
            model.setRowCount(0);
            for (Course c : dataStore.getEnrolledCoursesByStudent(currentUser.getUsername())) {
                model.addRow(new Object[]{c.getCourseCode(), c.getCourseName(), c.getCredit(), c.getInstructorUsername()});
            }
        };
        refreshMyCourses.run();

        JButton dropBtn = new JButton("Drop Selected Course");
        dropBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a course to drop.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String courseCode = (String) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to drop " + courseCode + "?", "Confirm Drop", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dataStore.removeEnrollment(currentUser.getUsername(), courseCode);
                refreshMyCourses.run();
                JOptionPane.showMessageDialog(this, "Course dropped successfully.");
            }
        });

        JPanel topBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topBtnPanel.add(dropBtn);

        panel.add(topBtnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    public JPanel createTranscriptPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Transcript"));

        String[] columns = {"Course Code", "Course Name", "Credit", "Midterm", "Final", "Average", "Letter Grade"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);

        JLabel gpaLabel = new JLabel();
        gpaLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        Runnable refreshTranscript = () -> {
            model.setRowCount(0);
            for (GradeRecord g : dataStore.getGradesByStudent(currentUser.getUsername())) {
                Course c = dataStore.findCourseByCode(g.getCourseCode());
                String courseName = c != null ? c.getCourseName() : g.getCourseCode();
                int credit = c != null ? c.getCredit() : 0;
                model.addRow(new Object[]{g.getCourseCode(), courseName, credit,
                        String.format("%.1f", g.getMidterm()), String.format("%.1f", g.getFinalExam()),
                        String.format("%.1f", g.calculateAverage()), g.getLetterGrade()});
            }
            double gpa = dataStore.calculateGPA(currentUser.getUsername());
            gpaLabel.setText("  GPA: " + String.format("%.2f", gpa));
        };
        refreshTranscript.run();

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTranscript.run());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(gpaLabel, BorderLayout.WEST);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(refreshBtn);
        topPanel.add(btnPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}
