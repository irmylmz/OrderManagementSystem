package view;

import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import business.BasketController;
import business.CartController;
import business.ProductController;
import core.Helper;
import entity.Basket;
import entity.Cart;
import entity.Customer;
import entity.Product;

public class CartUI extends JFrame{
	private JPanel container;
	private Customer customer;
	private BasketController basketController;
	private CartController cartController;
	private ProductController productController;
	
	// >>> UI fields
	private JLabel lbl_title;
	private JLabel lbl_customer_name;
	private JTextField fld_cart_date;
	private JTextArea  tarea_cart_note;
	private JButton    btn_cart;

	public CartUI(Customer customer) {
		this.customer = customer;
		this.basketController = new BasketController();
		this.cartController = new CartController();
		this.productController = new ProductController();
		
		
		container = new JPanel(new BorderLayout());
        setContentPane(container);
        setTitle("Create Cart");
        setSize(400, 600);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2;
        setLocation(x, y);
        
        // === CENTER PANEL (GridBag) ===
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
        lbl_title = new JLabel("Create Order");
        lbl_title.setFont(lbl_title.getFont().deriveFont(Font.BOLD, 16f));
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        form.add(lbl_title, gc);
        gc.gridwidth = 1;
        row++;

        // Müşteri
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Customer: "), gc);

        lbl_customer_name = new JLabel(customer != null ? customer.getName() : "-");
        gc.gridx = 1; gc.gridy = row++; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(lbl_customer_name, gc);

        // Sipariş Tarihi
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Order Date"), gc);

        fld_cart_date = new JTextField(java.time.LocalDate.now().toString()); 
        gc.gridx = 1; gc.gridy = row++; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(fld_cart_date, gc);

        // Sipariş Notu
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Order Note"), gc);

        tarea_cart_note = new JTextArea(5, 20);
        tarea_cart_note.setLineWrap(true);
        tarea_cart_note.setWrapStyleWord(true);
        JScrollPane spNote = new JScrollPane(
                tarea_cart_note,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        gc.gridx = 1; gc.gridy = row++; gc.weightx = 1; gc.weighty = 1; gc.fill = GridBagConstraints.BOTH;
        form.add(spNote, gc);

        // Buton
        btn_cart = new JButton("Create Order");
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        gc.weightx = 0; gc.weighty = 0; gc.fill = GridBagConstraints.NONE; gc.anchor = GridBagConstraints.CENTER;
        form.add(btn_cart, gc);
        gc.gridwidth = 1;

        // === Buton davranışı ===
        btn_cart.addActionListener(e -> {
            // 1) Müşteri kontrolü
            if (customer == null || customer.getId() == 0) {
                Helper.showMessage("Please select a valid customer!");
                return;
            }

            // 2) Sepet
            ArrayList<Basket> baskets = this.basketController.findAll();
            if (baskets == null || baskets.isEmpty()) {
                Helper.showMessage("Please add items to your cart!");
                dispose();
                return;
            }

            // 3) Tarih (ISO: yyyy-MM-dd)
            LocalDate orderDate;
            try {
                orderDate = LocalDate.parse(fld_cart_date.getText().trim()); // ISO bekler
            } catch (Exception ex) {
                Helper.showMessage("The date format is incorrect! (YYYY-MM-DD)");
                return;
            }

            // 4) Siparişleri oluştur
            boolean anySaved = false;
            String note = tarea_cart_note.getText();

            for (Basket basket : baskets) {
                Product p = basket.getProduct();
                if (p == null) continue;

                // stok yoksa atla
                if (p.getStock() <= 0) {
                    // İstersen kullanıcıya bilgi verebilirsin:
                    // Helper.showMessage(p.getName() + " is out of stock, skipped.");
                    continue;
                }

                Cart cart = new Cart();
                cart.setCustomerId(this.customer.getId());
                cart.setProductId(p.getId());
                cart.setPrice(p.getPrice());
                cart.setDate(orderDate);          // LocalDate (ISO) -> CartDao.save içinde java.sql.Date'e çeviriyorsun
                cart.setNote(note);

                if (this.cartController.save(cart)) {
                    anySaved = true;
                    // stok 1 düş
                    p.setStock(p.getStock() - 1);
                    this.productController.update(p);
                }
            }

            if (anySaved) {
                this.basketController.clear();
                Helper.showMessage("done");
                dispose();
            } else {
                Helper.showMessage("No items were saved (all were out of stock?).");
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        this.lbl_customer_name.setText(this.customer.getName());
        
        /*
        if(customer.getId() == 0) {
        	Helper.showMessage("Please select a valid customer!");
        }
        
        ArrayList<Basket> baskets = this.basketController.findAll();
        if(baskets.size() == 0) {
        	Helper.showMessage("Please add items to your cart!");
        }
        */
	}
	
	/*
	
	private void createUIComponents() throws ParseException {
	    this.fld_cart_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    this.fld_cart_date.setText(formatter.format(LocalDate.now()));
	}
*/
}
