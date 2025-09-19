package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import business.BasketController;
import business.CustomerController;
import business.ProductController;
import core.Helper;
import core.Item;
import entity.Basket;
import entity.Customer;
import entity.Product;
import entity.User;

public class DashboardUI extends JFrame {
    private JPanel container;
    private User user;
    private CustomerController customerController;
    private ProductController productController;
    private BasketController basketController;

    // Tablo bileşenleri
    private DefaultTableModel tbl_customer;
    private JTable tblCustomers;

    // Üst filtre şeridi bileşenleri
    private JTextField txtCustomerName;
    private JComboBox<Customer.TYPE> cmbLabel;   
    private JButton btnSearch, btnClear, btnNew;
    private JPopupMenu popup_customer = new JPopupMenu();
    
    // === Product tab bileşenleri ===
    private DefaultTableModel tbl_product;
    private JTable tblProducts;

    private JTextField txtProductName;
    private JTextField txtProductCode;
    private JComboBox<Item> cmbProductStock; 
    private JButton btnProductSearch, btnProductClear, btnProductNew;

    private JPopupMenu popup_product = new JPopupMenu();
    
    // --- Basket tab bileşenleri (EKLE) ---
    private DefaultTableModel tbl_basket;
    private JTable tblBasket;

    private JComboBox<Item> cmbBasketCustomer;
    private JLabel lblBasketPrice;
    private JLabel lblBasketCount;
    private JButton btnBasketReset;
    private JButton btnBasketCreate;
    

    public DashboardUI(User user) {
        this.user = user;
        this.customerController = new CustomerController();
        this.productController = new ProductController();
        this.basketController =  new BasketController();
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
        
        JTabbedPane tabs = new JTabbedPane();
        container.add(tabs, BorderLayout.CENTER);
        // EKLE ️
        tabs.addTab("Customers", customersPanel);
        // Zaten var:
        tabs.addTab("Products", buildProductsPanel());
        tabs.addTab("Basket", buildBasketPanel());   // EKLE

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
    				loadBasketCustomerCombo();
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
        				loadBasketCustomerCombo();
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
                    	loadBasketCustomerCombo();
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
	
	private JPanel buildProductsPanel() {
	    JPanel productsPanel = new JPanel(new GridBagLayout());
	    productsPanel.setBorder(new TitledBorder("Products"));
	    GridBagConstraints pgc = new GridBagConstraints();
	    pgc.insets = new Insets(8, 8, 8, 8);

	    // ------ Üst filtre şeridi ------
	    JPanel filterBar = new JPanel(new GridBagLayout());
	    GridBagConstraints fg = new GridBagConstraints();
	    fg.insets = new Insets(4, 6, 4, 6);
	    fg.gridy = 0;

	    int col = 0;

	    // Ürün Adı
	    fg.gridx = col++; fg.weightx = 0; fg.fill = GridBagConstraints.NONE; fg.anchor = GridBagConstraints.LINE_START;
	    filterBar.add(new JLabel("Product Name"), fg);

	    txtProductName = new JTextField(16);
	    fg.gridx = col++; fg.weightx = 0.4; fg.fill = GridBagConstraints.HORIZONTAL;
	    filterBar.add(txtProductName, fg);

	    // Ürün Kodu
	    fg.gridx = col++; fg.weightx = 0; fg.fill = GridBagConstraints.NONE; fg.anchor = GridBagConstraints.LINE_START;
	    filterBar.add(new JLabel("Product Code"), fg);

	    txtProductCode = new JTextField(14);
	    fg.gridx = col++; fg.weightx = 0.25; fg.fill = GridBagConstraints.HORIZONTAL;
	    filterBar.add(txtProductCode, fg);

	    // Stok Durumu
	    fg.gridx = col++; fg.weightx = 0; fg.fill = GridBagConstraints.NONE; fg.anchor = GridBagConstraints.LINE_START;
	    filterBar.add(new JLabel("Stock"), fg);

	    cmbProductStock = new JComboBox<>();
	    cmbProductStock.addItem(new Item(1, "In stock"));
	    cmbProductStock.addItem(new Item(2, "Out of stock"));
	    cmbProductStock.setSelectedItem(null);
	    fg.gridx = col++; fg.weightx = 0.25; fg.fill = GridBagConstraints.HORIZONTAL;
	    filterBar.add(cmbProductStock, fg);
	    

	    // Butonlar
	    btnProductSearch = new JButton("Search");
	    fg.gridx = col++; fg.weightx = 0; fg.fill = GridBagConstraints.NONE;
	    filterBar.add(btnProductSearch, fg);

	    btnProductClear = new JButton("Clear");
	    fg.gridx = col++;
	    filterBar.add(btnProductClear, fg);

	    btnProductNew = new JButton("Add New");
	    fg.gridx = col++;
	    filterBar.add(btnProductNew, fg);

	    // Filtre barı ekle
	    pgc.gridx = 0; pgc.gridy = 0; pgc.weightx = 1; pgc.weighty = 0; pgc.fill = GridBagConstraints.HORIZONTAL;
	    productsPanel.add(filterBar, pgc);

	    // ------ Tablo ------
	    String[] cols = {"ID", "Name", "Code", "Price", "Stock"};
	    tbl_product = new DefaultTableModel(cols, 0) {
	        @Override public boolean isCellEditable(int r, int c) { return false; }
	    };

	    tblProducts = new JTable(tbl_product);
	    tblProducts.setFillsViewportHeight(true);
	    tblProducts.getTableHeader().setReorderingAllowed(false);
	    tblProducts.getColumnModel().getColumn(0).setMaxWidth(60);
	    tblProducts.setAutoCreateRowSorter(true);

	    JScrollPane sp = new JScrollPane(tblProducts);
	    pgc.gridx = 0; pgc.gridy = 1; pgc.gridwidth = 1;
	    pgc.weightx = 1; pgc.weighty = 1; pgc.fill = GridBagConstraints.BOTH;
	    productsPanel.add(sp, pgc);
	    
	    loadProductTable(productController.findAll());
        loadProductPopupMenu();
        loadProductButtonEvent();
        

	    // ------ Basit davranışlar ------
	    btnProductClear.addActionListener(e -> {
	    	ArrayList<Product> filteredProducts = this.productController.filter(
	    	        this.txtProductName.getText(),
	    	        this.txtProductCode.getText(),
	    	        (Item) this.cmbProductStock.getSelectedItem()
	    	    );
	    	    loadProductTable(null);
	       
	    });
        
	    return productsPanel;
	}
	
	private void loadProductTable(List<Product> products) {
	    tbl_product.setRowCount(0);
	    if (products == null) products = productController.findAll();

	    for (Product p : products) {
	        tbl_product.addRow(new Object[]{
	                p.getId(),
	                p.getName(),
	                p.getCode(),
	                p.getPrice(),   // BigDecimal
	                p.getStock()
	        });
	    }
	}
	
	private void loadProductPopupMenu() {
	    // Menü maddeleri
	    JMenuItem miUpdate = new JMenuItem("Update");
	    JMenuItem miDelete = new JMenuItem("Delete");
	    JMenuItem miAddToBasket = new JMenuItem("Add to Cart"); 
	    popup_product.add(miUpdate);
	    popup_product.add(miDelete);
	    popup_product.add(miAddToBasket); 

	    tblProducts.setComponentPopupMenu(popup_product);

	    // Sağ tıkta altındaki satırı seç
	    MouseAdapter selector = new MouseAdapter() {
	        private void maybeSelect(MouseEvent e) {
	            if (e.isPopupTrigger()) {
	                int row = tblProducts.rowAtPoint(e.getPoint()); // DOĞRU TABLO
	                if (row >= 0 && row < tblProducts.getRowCount()) {
	                    tblProducts.setRowSelectionInterval(row, row);
	                } else {
	                    tblProducts.clearSelection();
	                }
	            }
	        }
	        @Override public void mousePressed(MouseEvent e)  { maybeSelect(e); }
	        @Override public void mouseReleased(MouseEvent e) { maybeSelect(e); }
	    };
	    tblProducts.addMouseListener(selector);

	    // Seçim modu (tek satır)
	    tblProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	    // === Update ===
	    miUpdate.addActionListener(e -> {
	        int viewRow = tblProducts.getSelectedRow();
	        if (viewRow >= 0) {
	            int modelRow = tblProducts.convertRowIndexToModel(viewRow);
	            int selectId = Integer.parseInt(tbl_product.getValueAt(modelRow, 0).toString());

	            ProductUI productUI = new ProductUI(this.productController.getById(selectId));
	            productUI.addWindowListener(new WindowAdapter() {
	                @Override
	                public void windowClosed(WindowEvent e2) {
	                    loadProductTable(null);
	                    loadBasketTable();
	                }
	            });
	        }
	    });

	    // === Delete ===
	    miDelete.addActionListener(e -> {
	        int viewRow = tblProducts.getSelectedRow();
	        if (viewRow >= 0) {
	            int modelRow = tblProducts.convertRowIndexToModel(viewRow);
	            int selectId = Integer.parseInt(tbl_product.getValueAt(modelRow, 0).toString());

	            if (Helper.confirm("sure")) {
	                if (this.productController.delete(selectId)) {
	                    Helper.showMessage("done");
	                    loadProductTable(null);
	                    loadBasketTable();
	                } else {
	                    Helper.showMessage("error");
	                }
	            }
	        }
	    });

	    // === Add to Cart ===
	    miAddToBasket.addActionListener(e -> {
	        int viewRow = tblProducts.getSelectedRow();
	        if (viewRow >= 0) {
	            int modelRow = tblProducts.convertRowIndexToModel(viewRow);
	            int selectId = Integer.parseInt(tbl_product.getValueAt(modelRow, 0).toString());

	            Product basketProduct = this.productController.getById(selectId);
	            if (basketProduct.getStock() <= 0) {
	                Helper.showMessage("This product is out of stock!");
	            } else {
	            	Basket basket = new Basket(basketProduct.getId());
	            	if(this.basketController.save(basket)) {
	            		Helper.showMessage("done");
	            		loadBasketTable();
	            		// Helper.showMessage("The product has been added to your cart: " + basketProduct.getName());
	            	}else {
	            		Helper.showMessage("error");
	            	}
	            }
	        }
	    });
	}
	
	private void loadProductButtonEvent() {
	    this.btnProductNew.addActionListener(e -> {
	        ProductUI productUI = new ProductUI(new Product());
	        productUI.addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosed(WindowEvent e) {
	                loadProductTable(null);
	                loadBasketTable();
	            }
	        });
	    });
	    
	    this.btnProductSearch.addActionListener(e -> {
	        ArrayList<Product> filteredProducts = this.productController.filter(
	        		txtProductName.getText(),
	        		txtProductCode.getText(),
	        		(Item)cmbProductStock.getSelectedItem()
	        		);
	        loadProductTable(filteredProducts);
	        
	    });
	}
	private JPanel buildBasketPanel() {
	    JPanel basketPanel = new JPanel(new GridBagLayout());
	    basketPanel.setBorder(new TitledBorder("Create Basket"));
	    GridBagConstraints bgc = new GridBagConstraints();
	    bgc.insets = new Insets(8, 8, 8, 8);

	    // === Üst bar ===
	    JPanel topBar = new JPanel(new GridBagLayout());
	    GridBagConstraints tg = new GridBagConstraints();
	    tg.insets = new Insets(4, 6, 4, 6);
	    tg.gridy = 0;

	    int col = 0;

	    // Müşteri seçimi
	    tg.gridx = col++; tg.weightx = 0; tg.fill = GridBagConstraints.NONE; tg.anchor = GridBagConstraints.LINE_START;
	    topBar.add(new JLabel("Choose Customer"), tg);

	    cmbBasketCustomer = new JComboBox<>();
	    for (Customer c : customerController.findAll()) {
	        // Item(key=id, value=name)
	        cmbBasketCustomer.addItem(new Item(c.getId(), c.getName()));
	    }
	    cmbBasketCustomer.setSelectedItem(null);
	    tg.gridx = col++; tg.weightx = 0.4; tg.fill = GridBagConstraints.HORIZONTAL;
	    topBar.add(cmbBasketCustomer, tg);

	    // Toplam Tutar
	    tg.gridx = col++; tg.weightx = 0; tg.fill = GridBagConstraints.NONE;
	    topBar.add(new JLabel("Total Amount"), tg);

	    lblBasketPrice = new JLabel("0 TL");
	    tg.gridx = col++; tg.weightx = 0; tg.fill = GridBagConstraints.NONE;
	    topBar.add(lblBasketPrice, tg);

	    // Ürün Sayısı
	    tg.gridx = col++; tg.weightx = 0; tg.fill = GridBagConstraints.NONE;
	    topBar.add(new JLabel("Number of Products"), tg);

	    lblBasketCount = new JLabel("0 Quantity");
	    tg.gridx = col++; tg.weightx = 0; tg.fill = GridBagConstraints.NONE;
	    topBar.add(lblBasketCount, tg);

	    // Butonlar
	    btnBasketReset = new JButton("Clear");
	    tg.gridx = col++; tg.weightx = 0; tg.fill = GridBagConstraints.NONE;
	    topBar.add(btnBasketReset, tg);

	    btnBasketCreate = new JButton("Create Basket");
	    tg.gridx = col++; tg.weightx = 0; tg.fill = GridBagConstraints.NONE;
	    topBar.add(btnBasketCreate, tg);

	    // Üst barı panele ekle
	    bgc.gridx = 0; bgc.gridy = 0; bgc.weightx = 1; bgc.weighty = 0; bgc.fill = GridBagConstraints.HORIZONTAL;
	    basketPanel.add(topBar, bgc);

	    // === Tablo ===
	    String[] cols = {"ID", "Product", "Code", "Price"};
	    tbl_basket = new DefaultTableModel(cols, 0) {
	        @Override public boolean isCellEditable(int r, int c) { return false; }
	    };
	    tblBasket = new JTable(tbl_basket);
	    tblBasket.setFillsViewportHeight(true);
	    tblBasket.getTableHeader().setReorderingAllowed(false);
	    tblBasket.getColumnModel().getColumn(0).setMaxWidth(60);
	    tblBasket.setAutoCreateRowSorter(true);

	    JScrollPane sp = new JScrollPane(tblBasket);
	    bgc.gridx = 0; bgc.gridy = 1; bgc.gridwidth = 1;
	    bgc.weightx = 1; bgc.weighty = 1; bgc.fill = GridBagConstraints.BOTH;
	    basketPanel.add(sp, bgc);

	    loadBasketTable();
	    loadBasketButtonEvent();
	    loadBasketCustomerCombo();
	    
	    return basketPanel;
	}
	
	private void loadBasketCustomerCombo() {
		ArrayList<Customer> customers = this.customerController.findAll();
		this.cmbBasketCustomer.removeAllItems();
		for(Customer customer : customers) {
			int comboKey = customer.getId();
			String comboValue = customer.getName();
			this.cmbBasketCustomer.addItem(new Item(comboKey, comboValue));
		}
		this.cmbBasketCustomer.setSelectedItem(null);
	}
	
	private void loadBasketTable() {
	    tbl_basket.setRowCount(0);
	    try {
	        List<Basket> rows = basketController.findAll();
	        BigDecimal totalPrice = BigDecimal.ZERO;
	        int totalCount = 0;

	        for (Basket b : rows) {
	            Product p = b.getProduct();
	            if (p != null) {
	                tbl_basket.addRow(new Object[]{
	                        b.getId(),
	                        p.getName(),
	                        p.getCode(),
	                        p.getPrice()
	                });
	                totalPrice = totalPrice.add(p.getPrice());
	                totalCount++;
	            }
	        }
	        this.lblBasketPrice.setText(totalPrice + " TL");
	        this.lblBasketCount.setText(totalCount + " Adet");

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    updateBasketTotals(); 
	}
	
	private void loadBasketButtonEvent() {
		btnBasketCreate.addActionListener(e -> {
	        Item selected = (Item) cmbBasketCustomer.getSelectedItem();
	        if (selected == null) {
	            Helper.showMessage("Please choose the costomer!");
	            return;
	        }
	        if (tbl_basket.getRowCount() == 0) {
	            Helper.showMessage("The basket is empty!");
	            return;
	        }
	        int customerId = selected.getKey();
	        // Burada kendi sipariş oluşturma akışını çağır:
	        // boolean ok = orderController.createOrder(customerId);
	        // Şimdilik sadece bilgi mesajı:
	        Helper.showMessage("Sipariş alındı. Customer ID: " + customerId);
	        // İstersen sepeti sıfırla:
	        // basketController.clear();
	        tbl_basket.setRowCount(0);
	        updateBasketTotals();
	    });
		
		btnBasketReset.addActionListener(e -> {
	        if(this.basketController.clear()) {
	        	Helper.showMessage("done");
	        	loadBasketTable();
	        }else {
	        	Helper.showMessage("error");
	        }
	        tbl_basket.setRowCount(0);
	        updateBasketTotals();
	    });
	}
	
	private void updateBasketTotals() {
	    int count = tbl_basket.getRowCount();
	    lblBasketCount.setText(count + " Adet");

	    java.math.BigDecimal total = java.math.BigDecimal.ZERO;
	    for (int i = 0; i < count; i++) {
	        Object priceObj = tbl_basket.getValueAt(i, 3);
	        if (priceObj instanceof java.math.BigDecimal) {
	            total = total.add((java.math.BigDecimal) priceObj);
	        } else if (priceObj != null) {
	            try {
	                total = total.add(new java.math.BigDecimal(priceObj.toString()));
	            } catch (NumberFormatException ignore) { }
	        }
	    }
	    lblBasketPrice.setText(total + " TL");
	}
}