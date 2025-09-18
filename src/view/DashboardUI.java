package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import business.CustomerController;
import core.Helper;
import entity.Customer;
import entity.User;

public class DashboardUI extends JFrame {
    private JPanel container;
    private User user;
    private CustomerController customerController;

    // Tablo bileşenleri
    private DefaultTableModel tbl_customer;
    private JTable tblCustomers;

    // Üst filtre şeridi bileşenleri
    private JTextField txtCustomerName;
    private JComboBox<Customer.TYPE> cmbLabel;   
    private JButton btnSearch, btnClear, btnNew;
    private JPopupMenu popup_customer = new JPopupMenu();

    public DashboardUI(User user) {
        this.user = user;
        this.customerController = new CustomerController();
        if (user == null) {
            Helper.showMessage("error");
            dispose();
            return; // önemli: devam etme
        }

        // Ana container
        container = new JPanel(new BorderLayout());
        this.setContentPane(container);

        // === ÜST PANEL (Welcome + Logout) - GridBag
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.weighty = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblWelcome = new JLabel("Welcome, " + user.getName());
        lblWelcome.setFont(lblWelcome.getFont().deriveFont(Font.BOLD, 16f));
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 1; gc.anchor = GridBagConstraints.WEST;
        topPanel.add(lblWelcome, gc);

        JButton btnLogout = new JButton("Logout");
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 0; gc.anchor = GridBagConstraints.EAST;
        topPanel.add(btnLogout, gc);

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginUI(); // LoginUI kendi içinde setVisible(true) çağırıyor
        });

        container.add(topPanel, BorderLayout.NORTH);

        // === ORTA PANEL: Customers (filtre bar + tablo)
        JPanel customersPanel = new JPanel(new GridBagLayout());
        customersPanel.setBorder(new TitledBorder("Customers"));
        GridBagConstraints cgc = new GridBagConstraints();
        cgc.insets = new Insets(8, 8, 8, 8);

        // ---- 1) Filtre Şeridi (videodaki üst gri bar)
        JPanel filterBar = new JPanel(new GridBagLayout());
        GridBagConstraints fg = new GridBagConstraints();
        fg.insets = new Insets(4, 6, 4, 6);
        fg.gridy = 0;

        int col = 0;

        // "Customer Name" label
        fg.gridx = col++; fg.weightx = 0; fg.fill = GridBagConstraints.NONE; fg.anchor = GridBagConstraints.LINE_START;
        filterBar.add(new JLabel("Customer Name"), fg);

        // TextField
        txtCustomerName = new JTextField(16);
        fg.gridx = col++; fg.weightx = 0.4; fg.fill = GridBagConstraints.HORIZONTAL;
        filterBar.add(txtCustomerName, fg);

        // "Label" (biz Type gibi kullanıyoruz)
        fg.gridx = col++; fg.weightx = 0; fg.fill = GridBagConstraints.NONE; fg.anchor = GridBagConstraints.LINE_START;
        filterBar.add(new JLabel("Type"), fg);

        //cmbLabel = new JComboBox<>(new String[]{"All", "Retail", "Corporate"});
        cmbLabel = new JComboBox<>(Customer.TYPE.values());
        cmbLabel.setSelectedItem(null);
        fg.gridx = col++; fg.weightx = 0.25; fg.fill = GridBagConstraints.HORIZONTAL;
        filterBar.add(cmbLabel, fg);

        // Search
        btnSearch = new JButton("Search");
        fg.gridx = col++; fg.weightx = 0; fg.fill = GridBagConstraints.NONE;
        filterBar.add(btnSearch, fg);

        // Clear
        btnClear = new JButton("Clear");
        fg.gridx = col++;
        filterBar.add(btnClear, fg);

        // New
        btnNew = new JButton("New");
        fg.gridx = col++;
        filterBar.add(btnNew, fg);
        
        loadCustomerButtonEvent();

        // Filtre barı customersPanel'e ekle (üst satır)
        cgc.gridx = 0; cgc.gridy = 0; cgc.weightx = 1; cgc.weighty = 0; cgc.fill = GridBagConstraints.HORIZONTAL;
        customersPanel.add(filterBar, cgc);

        // ---- 2) Tablo modeli
        String[] columns = {"ID", "Name", "Type", "Phone", "Mail", "Address"};
        tbl_customer = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // ---- JTable
        tblCustomers = new JTable(tbl_customer);
        tblCustomers.setFillsViewportHeight(true);
        tblCustomers.getTableHeader().setReorderingAllowed(false);
        tblCustomers.getColumnModel().getColumn(0).setMaxWidth(60);
        tblCustomers.setAutoCreateRowSorter(true);
        //tblCustomers.setEnabled(false);

        JScrollPane sp = new JScrollPane(tblCustomers);

        // Tabloyu customersPanel'e ekle (alt satır)
        cgc.gridx = 0; cgc.gridy = 1; cgc.gridwidth = 1;
        cgc.weightx = 1; cgc.weighty = 1; cgc.fill = GridBagConstraints.BOTH;
        customersPanel.add(sp, cgc);

        container.add(customersPanel, BorderLayout.CENTER);

        // Frame ayarları
        this.setTitle("Customer Management System - Dashboard");
        this.setSize(1000, 600);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height) / 2;
        this.setLocation(x, y);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- DB'den doldur
        loadCustomerTable(customerController.findAll());
        
        loadCustomerPopupMenu();
      

        this.setVisible(true);
    }
    
    private void loadCustomerButtonEvent() {
    	this.btnNew.addActionListener(e -> {
    		CustomerUI customerUI = new CustomerUI(new Customer());
    		customerUI.addWindowListener(new WindowAdapter() {
    			@Override
    			public void windowClosed(WindowEvent e) {
    				loadCustomerTable(null);
    			}
			});
    	});
    	
    	this.btnSearch.addActionListener(e -> {
    		ArrayList<Customer> filteredCustomers = this.customerController.filter(
    				this.txtCustomerName.getText(), 
    				(Customer.TYPE)this.cmbLabel.getSelectedItem()
    				);
    		loadCustomerTable(filteredCustomers);
    	});
    	
    	this.btnClear.addActionListener(e -> {
            loadCustomerTable(null);
            this.txtCustomerName.setText(null);
            this.cmbLabel.setSelectedItem(null);
        });
    }

    private void loadCustomerPopupMenu() {
        // Menü maddeleri
        JMenuItem miUpdate = new JMenuItem("Update");
        JMenuItem miDelete = new JMenuItem("Delete");
        popup_customer.add(miUpdate);
        popup_customer.add(miDelete);

        // Popup menüyü TABLOYA bağla (modele değil!)
        tblCustomers.setComponentPopupMenu(popup_customer);

        // Sağ tıkta altındaki satırı seç
        MouseAdapter selector = new MouseAdapter() {
            private void maybeSelect(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tblCustomers.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < tblCustomers.getRowCount()) {
                        tblCustomers.setRowSelectionInterval(row, row);
                    } else {
                        tblCustomers.clearSelection();
                    }
                }
            }
            @Override public void mousePressed(MouseEvent e)  { maybeSelect(e); }
            @Override public void mouseReleased(MouseEvent e) { maybeSelect(e); }
        };
        tblCustomers.addMouseListener(selector);

        // Seçim modu (tek satır)
        tblCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        miUpdate.addActionListener(e -> {
            int selectedRow = tblCustomers.getSelectedRow();
            if (selectedRow >= 0) {
                int selectId = Integer.parseInt(
                    tbl_customer.getValueAt(selectedRow, 0).toString()
                );
                CustomerUI customerUI = new CustomerUI(this.customerController.getById(selectId));
                customerUI.addWindowListener(new WindowAdapter() {
        			@Override
        			public void windowClosed(WindowEvent e) {
        				loadCustomerTable(null);
        			}
    			});
            }
        });
        
        miDelete.addActionListener(e -> {
            int selectedRow = tblCustomers.getSelectedRow();
            if (selectedRow >= 0) {
                int selectId = Integer.parseInt(
                    tbl_customer.getValueAt(selectedRow, 0).toString()
                );
                if(Helper.confirm("sure")) {
                	if(this.customerController.delete(selectId)) {
                    	Helper.showMessage("done");
                    	loadCustomerTable(null);
                    }else {
                    	Helper.showMessage("error");
                    }
                }
            }
        });
    }

	private void loadCustomerTable(List<Customer> customers) {
        tbl_customer.setRowCount(0); // önce temizle
        if (customers == null) customers = this.customerController.findAll();
        
        // DefaultTableModel clearModel = (DefaultTableModel) this.tbl_customer.getModel();      ÇALIŞMADI
        //((DefaultTableModel) tblCustomers.getModel()).setRowCount(0);                          İSTERSEN BUNU YAP

        for (Customer customer : customers) {
            String typeText = (customer.getType() == null) ? "" : customer.getType().name();
            // String ise: String typeText = customer.getType();

            tbl_customer.addRow(new Object[]{
                    customer.getId(),
                    customer.getName(),
                    typeText,
                    customer.getPhone(),
                    customer.getMail(),
                    customer.getAddress()
            });
        }
    }
}