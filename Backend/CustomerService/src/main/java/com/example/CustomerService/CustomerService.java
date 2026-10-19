package com.example.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerdb;
    public String savetocustomerdb(Requestdto request){
        CustomerEntity customer =new CustomerEntity();
        customer.setCustomerId(request.getCustomerId());
        customer.setCustomerName(request.getCustomerName());
        customer.setCustomerEmail(request.getEmail());
        customer.setCreatedAt(request.getCreatedDate());
        System.out.println("Customer Controller:");
        System.out.println("ID: " + request.getCustomerId());
        System.out.println("NAME: " + request.getCustomerName());
        System.out.println("EMAIL: " + request.getEmail());
        System.out.println("DATE: " + request.getCreatedDate());
        customerdb.save(customer);
        return "customer registered";
    }

    public Optional<CustomerEntity> searchbyId(String id) {
        return customerdb.findById(id);
    }
    public Optional<CustomerEntity> updateCustomer(String id, UpdateCustomerDto request) {

        Optional<CustomerEntity> optionalCustomer =
                customerdb.findById(id);

        if (optionalCustomer.isEmpty()) {
            return optionalCustomer;
        }

        CustomerEntity customer = optionalCustomer.get();

        customer.setCustomerName(request.getCustomer_name());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setPincode(request.getPincode());

        customerdb.save(customer);

        return Optional.of(customer);
    }
    public String deleteCustomer(String id) {

        if (!customerdb.existsById(id)) {
            return "Customer not found";
        }

        customerdb.deleteById(id);

        return "Customer deleted successfully";
    }
}