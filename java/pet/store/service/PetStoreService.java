package pet.store.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.PetStore;


import java.util.*;

@Service
public class PetStoreService {
    @Autowired
    private PetStoreDao petStoreDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private CustomerDao customerDao;

    @Transactional(readOnly = false)
    public PetStoreData savePetStore(PetStoreData petStoreData) {
        Long petStoreId = petStoreData.getPetStoreId();
        PetStore petStore = findOrCreatePetStore(petStoreId);

        copyPetStoreFields(petStore, petStoreData);
        return new PetStoreData(petStoreDao.save(petStore));

    }

    private PetStore findOrCreatePetStore(Long petStoreId) {
        PetStore petStore;

        if (Objects.isNull(petStoreId)) {
            petStore = new PetStore();
        } else {
            petStore = findPetStoreById(petStoreId);
        }
        return petStore;

    }

    private PetStore findPetStoreById(Long petStoreId) {
        return petStoreDao.findById(petStoreId).orElseThrow(() -> new NoSuchElementException(
                "Pet store with ID=" + petStoreId + " does not exist."));
    }

    private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
        petStore.setPetStoreId(petStoreData.getPetStoreId());
        petStore.setPetStoreName(petStoreData.getPetStoreName());
        petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
        petStore.setPetStoreCity(petStoreData.getPetStoreCity());
        petStore.setPetStoreState(petStoreData.getPetStoreState());
        petStore.setPetStoreZip(petStoreData.getPetStoreZip());
        petStore.setPetStorePhone(petStoreData.getPetStorePhone());

    }

    @Transactional(readOnly = false)
    public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {

        PetStore petStore = findPetStoreById(petStoreId);

        Long employeeId = petStoreEmployee.getEmployeeId();

        Employee employee = findOrCreateEmployee(employeeId, petStoreId);

        copyEmployeeFields(employee, petStoreEmployee);

        employee.setPetStore(petStore);

        petStore.getEmployees().add(employee);

        Employee dbEmployee = employeeDao.save(employee);

        return new PetStoreEmployee(dbEmployee);

    }

    public Employee findEmployeeById(Long petStoreId, Long employeeId) {
        Employee employee = employeeDao.findById(employeeId).orElse(null);

        if (employee == null) {
            throw new NoSuchElementException("Employee with ID=" + employeeId + " does not exist.");
        }

        if (!employee.getPetStore().getPetStoreId().equals(petStoreId)) {
            throw new IllegalArgumentException(
                    "Employee with ID=" + employeeId + " does not exist in pet store with ID=" + petStoreId);
        }

        return employee;
    }

    private Employee findOrCreateEmployee(Long employeeId, Long petStoreId) {

        if (Objects.isNull(employeeId)) {
            return new Employee();
        }
        return findEmployeeById(petStoreId, employeeId);

    }

    private void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
        employee.setEmployeeId(petStoreEmployee.getEmployeeId());
        employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
        employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
        employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
        employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
    }


    @Transactional(readOnly = false)
    public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {

        PetStore petStore = findPetStoreById(petStoreId);

        Long customerId = petStoreCustomer.getCustomerId();

        Customer customer = findOrCreateCustomer(customerId, petStoreId);

        copyCustomerFields(customer, petStoreCustomer);

        customer.getPetStores().add(petStore);

        petStore.getCustomers().add(customer);

        Customer dbCustomer = customerDao.save(customer);

        return new PetStoreCustomer(dbCustomer);

    }

    private Customer findCustomerById(Long petStoreId, Long customerId) {
        Customer customer = customerDao.findById(customerId).orElse(null);

        if (customer == null) {
            throw new NoSuchElementException("Customer with ID=" + customerId + " does not exist.");
        }

        boolean petStoreFound = customer.getPetStores().stream()
                .anyMatch(petStore -> petStore.getPetStoreId().equals(petStoreId));

        if (!petStoreFound) {
            throw new IllegalArgumentException(
                    "PetStore with ID=" + petStoreId + " not found in customer's pet stores.");
        }

        return customer;

    }

    private Customer findOrCreateCustomer(Long customerId, Long petStoreId) {
        Customer customer;

        if (Objects.isNull(customerId)) {
            customer = new Customer();
        } else {
            customer = findCustomerById(petStoreId, customerId);
        }
        return customer;
    }

    private void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {
        customer.setCustomerId(petStoreCustomer.getCustomerId());
        customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
        customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
        customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
    }

    @Transactional(readOnly = true)
    public List<PetStoreData> retrieveAllPetStores() {
        List<PetStore> petStores = petStoreDao.findAll();
        List<PetStoreData> result = new LinkedList<>();

        for (PetStore petStore : petStores) {
            PetStoreData psd = new PetStoreData(petStore);

            psd.getCustomers().clear();
            psd.getEmployees().clear();

            result.add(psd);

        }
        return result;
    }
    @Transactional(readOnly = true)
    public PetStoreData retrievePetStoreById(Long petStoreId) {
        PetStore petStore = petStoreDao.findById(petStoreId).orElseThrow(() -> new NoSuchElementException
                ("PetStore with ID=" + petStoreId + " does not exist."));
        return new PetStoreData(petStore);
    }
    @Transactional(readOnly = false)
    public void deletePetStoreById(Long petStoreId) {
        PetStore petStore = petStoreDao.findById(petStoreId).orElseThrow(() -> new NoSuchElementException
                ("PetStore with ID=" + petStoreId + " does not exist."));
        petStoreDao.delete(petStore);
    }

}

