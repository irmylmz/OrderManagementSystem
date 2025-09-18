package view;

import javax.swing.*;
import business.CustomerController;
import core.Helper;
import entity.Customer;
import java.awt.*;

public class CustomerUI extends JFrame {
    private JPanel container;
    private CustomerController customerController;
    private Customer customer;

    private JTextField fldCustomerName;
    private JComboBox<Customer.TYPE> cmbCustomerType;
    private JTextField fldCustomerPhone;
    private JTextField fldCustomerMail;
    private JTextArea  tareaCustomerAddress;
    private JButton    btnCustomerSave;

    public CustomerUI(Customer customer) {
        this.customer = customer;
        this.customerController = new CustomerController();

        container = new JPanel(new BorderLayout());
        setContentPane(container);
        setTitle("Customer Add/Update");
        setSize(400, 600);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2;
        setLocation(x, y);

        // Orta form paneli (GridBag)
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        container.add(form, BorderLayout.CENTER);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.weightx = 1;

        int row = 0;

        // Başlık
        JLabel lblTitle = new JLabel(customer.getId() == 0 ? "Customer Add" : "Customer Update");
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(lblTitle, gc);
        gc.gridwidth = 1;
        row++;

        // Customer Name
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Customer Name"), gc);
        fldCustomerName = new JTextField();
        gc.gridx = 1; gc.gridy = row++; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(fldCustomerName, gc);

        // Customer Type
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Customer Type"), gc);
        cmbCustomerType = new JComboBox<>(Customer.TYPE.values());
        gc.gridx = 1; gc.gridy = row++; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(cmbCustomerType, gc);

        // Phone
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Phone"), gc);
        fldCustomerPhone = new JTextField();
        gc.gridx = 1; gc.gridy = row++; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(fldCustomerPhone, gc);

        // E-mail
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("E-mail"), gc);
        fldCustomerMail = new JTextField();
        gc.gridx = 1; gc.gridy = row++; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(fldCustomerMail, gc);

        // Address
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Customer Address"), gc);
        tareaCustomerAddress = new JTextArea(5, 20);
        tareaCustomerAddress.setLineWrap(true);
        tareaCustomerAddress.setWrapStyleWord(true);
        JScrollPane spAddress = new JScrollPane(tareaCustomerAddress,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gc.gridx = 1; gc.gridy = row++; gc.weightx = 1; gc.weighty = 1; gc.fill = GridBagConstraints.BOTH;
        form.add(spAddress, gc);

        // Save
        btnCustomerSave = new JButton("Save");
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        gc.weightx = 0; gc.weighty = 0; gc.fill = GridBagConstraints.NONE; gc.anchor = GridBagConstraints.CENTER;
        form.add(btnCustomerSave, gc);

        // -- Düzenleme modundaysan alanları şimdi doldur ---
        if (customer.getId() > 0) {
            fldCustomerName.setText(customer.getName());
            cmbCustomerType.setSelectedItem(customer.getType());
            fldCustomerPhone.setText(customer.getPhone());
            fldCustomerMail.setText(customer.getMail());
            tareaCustomerAddress.setText(customer.getAddress());
        }

        // --- Kaydet davranışı ---
        btnCustomerSave.addActionListener(e -> {
            JTextField[] checklist = { fldCustomerName, fldCustomerPhone };
            if (Helper.isFieldListEmpty(checklist)) {
                Helper.showMessage("fill");
                return;
            }
            if (!Helper.isFieldEmpty(fldCustomerMail) && !Helper.isEmailValid(fldCustomerMail.getText())) {
                Helper.showMessage("Please enter the valid e-mail");
                return;
            }

            // Formdan modele
            customer.setName(fldCustomerName.getText());
            customer.setType((Customer.TYPE) cmbCustomerType.getSelectedItem());
            customer.setPhone(fldCustomerPhone.getText());
            customer.setMail(fldCustomerMail.getText());
            customer.setAddress(tareaCustomerAddress.getText());

            boolean result = false;
            if (customer.getId() == 0) {
                result = customerController.save(customer);
            } else {
                result = customerController.update(customer);
            }

            Helper.showMessage(result ? "done" : "error");
            if (result) dispose();
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}