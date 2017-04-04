package model;

import exceptions.DiscountParseException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tour implements Payable {
    private List<Payment> payments = new ArrayList<>();
    private int persons;
    private LocalDateTime date;
    private double price;
    private Duration duration;
    private User user;

    public Tour(int persons, LocalDateTime date, double price, Duration duration, User user) {
        this.persons = persons;
        this.date = date;
        this.price = price;
        this.duration = duration;
        this.user = user;
    }

    @Override
    public void pay(Payment payment) {
		payments.add(payment);
    }

	@Override
	public PaymentStatus paymentStatus() throws DiscountParseException {
		return null;
	}

	public int getPersons() {
		return persons;
	}

	public void setPersons(int persons) {
		this.persons = persons;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	@Override
	public double totalPayment() {
		return 0;
	}
}
