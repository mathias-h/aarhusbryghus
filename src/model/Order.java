package model;

import exceptions.DiscountParseException;
import exceptions.InvaildPaymentAmount;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order implements Payable {
    private List<ProductOrder> products = new ArrayList<>();
    private List<RentalProductOrder> productsRental = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();
    private User user;
    private Pricelist pricelist;
    private Discount discount;
    private Customer customer;
    private LocalDate date = LocalDate.now();

    public Order(User user, Pricelist pricelist) {
        this.user = user;
        this.pricelist = pricelist;
    }

	public ProductOrder addProduct(Product product) {
		if (product instanceof DepositProduct) {
			return createRentalProductOrder((DepositProduct)product);
		}
		else {
			return createProductOrder(product);
		}
	}
	public ProductOrder removeProduct(Product product) {
		for (int i = 0; i < products.size(); i++) {
			ProductOrder po = products.get(i);
			
			if (po.getProduct().equals(product)) {
				products.remove(po);
				
				return po;
			}
		}
			
		for (int i = 0; i < productsRental.size(); i++) {
			ProductOrder po = productsRental.get(i);
			
			if (po.getProduct().equals(product)) {
				productsRental.remove(i);
				
				return po;
			}
		}
		
		return null;
	}
    
    public ProductOrder createProductOrder(Product product) {
        ProductOrder productOrder = new ProductOrder(product, this.pricelist);
        products.add(productOrder);
        return productOrder;
    }

    public RentalProductOrder createRentalProductOrder(DepositProduct product) {
        RentalProductOrder rentalProductOrder = new RentalProductOrder(product, this.pricelist);
        productsRental.add(rentalProductOrder);
        return rentalProductOrder;
    }

    public void setDiscount(String str) throws DiscountParseException {
        if (discount == null) {
            discount = new Discount();
        }
        discount.setDiscount(str);
    }

    public List<ProductOrder> getProductOrders() {
    	return new ArrayList<>(products);
    }
    
    public List<RentalProductOrder> getRentalProductOrders() {
    	return new ArrayList<>(productsRental);
    }
    
    public List<ProductOrder> getAllProducts(){
        List<ProductOrder> allProducts = new ArrayList<>(products);
        allProducts.addAll(productsRental);
        return allProducts;
    }

    public boolean hasRentalOrder() {
        return productsRental.size() > 0;
    }

    /**
     * Doesn't include deposit
     * @return
     */
    public double totalPrice() throws DiscountParseException {
        double sum = 0;
        for (ProductOrder productOrder : getAllProducts()) {
            sum += productOrder.price();
        }

        if (discount != null) {
            sum = discount.getPrice(sum);
        }
        return sum;
    }

    public boolean allRentalsReturned() {
        for (RentalProductOrder order : productsRental) {
            if (order.getAmount() != order.getReturned() + order.getUnused()
                + order.getNotReturned()) {
                return false;
            }
        }
        return true;
    }

    public double totalDepositAfterReturn() throws DiscountParseException {
        double sum = 0;
        for (RentalProductOrder productOrder : productsRental) {
            double deposit = ((DepositProduct) productOrder.getProduct()).getDeposit();
            sum += productOrder.getNotReturned() * deposit;
            sum -= productOrder.getUnused() * productOrder.individualPrice();
        }
        return sum;

    }

    public double totalDeposit() {
        double sum = 0;
        for (RentalProductOrder productOrder : productsRental) {
            sum += ((DepositProduct) productOrder.getProduct()).getDeposit()
                * productOrder.getAmount();
        }
        return sum;
    }

    public double totalPayment() {
        double sum = 0;
        for (Payment payment : payments) {
            sum += payment.getAmount();
        }
        return sum;
    }

    @Override
    public void pay(Payment payment) {
        payments.add(payment);
    }

    @Override
    public PaymentStatus paymentStatus() throws DiscountParseException, InvaildPaymentAmount {
        if (getAllProducts().size() == 0) {
            return PaymentStatus.UNPAID;
        }
        if (hasRentalOrder()) {
            if (allRentalsReturned()) {
                double returnPrice = totalDepositAfterReturn();
                if (totalPayment() == totalPrice() + returnPrice) {
                    return PaymentStatus.ORDERPAID;
                }
                else if (totalPayment() < totalPrice() + returnPrice) {
                    return PaymentStatus.UNPAID;
                }
                else if (totalPayment() > totalPrice() + returnPrice) {
                    return PaymentStatus.DEPOSITNOTPAIDBACK;
                }
            }
            else if (totalPayment() < totalDeposit()) {
                return PaymentStatus.UNPAID;
            }
            else if (totalPayment() >= totalDeposit()
                && totalPayment() <= totalPrice() + totalDeposit()) {
                return PaymentStatus.DEPOSITPAID;
            }
            else {
                throw new InvaildPaymentAmount("The order was overpaid");
            }
        }
        else {
            if (totalPayment() < totalPrice()) {
                return PaymentStatus.UNPAID;
            }
            else if (totalPayment() == totalPrice()) {
                return PaymentStatus.ORDERPAID;
            }
            else {
                throw new InvaildPaymentAmount("The order was overpaid");
            }
        }

        return null;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer c) {
        customer = c;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        try {
            return totalPrice() + "kr " + date.toString();
        }
        catch (DiscountParseException e) {
            return date.toString();
        }
    }

    public List<ProductOrder> getProducts() {
        return products;
    }

    public List<RentalProductOrder> getProductsRental() {
        return productsRental;
    }
}
