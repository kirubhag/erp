package krs.erp.controller.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.library.Author;
import krs.erp.model.library.LibraryHold;
import krs.erp.model.library.LibraryLoan;
import krs.erp.model.library.LibraryPO;
import krs.erp.model.library.LibraryPolicy;
import krs.erp.model.library.LibraryPurchaseRequest;
import krs.erp.model.library.LibraryResource;
import krs.erp.model.library.Publisher;
import krs.erp.model.library.ResourceItem;
import krs.erp.repository.library.AuthorRepository;
import krs.erp.repository.library.LibraryHoldRepository;
import krs.erp.repository.library.LibraryLoanRepository;
import krs.erp.repository.library.LibraryPORepository;
import krs.erp.repository.library.LibraryPolicyRepository;
import krs.erp.repository.library.LibraryPurchaseRequestRepository;
import krs.erp.repository.library.LibraryResourceRepository;
import krs.erp.repository.library.PublisherRepository;
import krs.erp.repository.library.ResourceItemRepository;

@RestController
@RequestMapping("/api/library")
@CrossOrigin(origins = "*")
public class LibraryController {

    @Autowired
    private AuthorRepository authorRepository;
    
    @Autowired
    private PublisherRepository publisherRepository;
    
    @Autowired
    private LibraryResourceRepository libraryResourceRepository;
    
    @Autowired
    private ResourceItemRepository resourceItemRepository;
    
    @Autowired
    private LibraryLoanRepository libraryLoanRepository;
    
    @Autowired
    private LibraryHoldRepository libraryHoldRepository;
    
    @Autowired
    private LibraryPolicyRepository libraryPolicyRepository;
    
    @Autowired
    private LibraryPurchaseRequestRepository libraryPurchaseRequestRepository;
    
    @Autowired
    private LibraryPORepository libraryPORepository;

    // === Authors CRUD ===
    @GetMapping("/authors")
    public ResponseEntity<Page<Author>> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(authorRepository.findAll(pageable));
    }
    
    @GetMapping("/authors/{id}")
    public ResponseEntity<Author> getAuthor(@PathVariable Long id) {
        return authorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/authors")
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        return ResponseEntity.ok(authorRepository.save(author));
    }
    
    @PutMapping("/authors/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, @RequestBody Author author) {
        if (!authorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        author.setId(id);
        return ResponseEntity.ok(authorRepository.save(author));
    }
    
    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        if (!authorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        authorRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Publishers CRUD ===
    @GetMapping("/publishers")
    public ResponseEntity<Page<Publisher>> getAllPublishers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(publisherRepository.findAll(pageable));
    }
    
    @GetMapping("/publishers/{id}")
    public ResponseEntity<Publisher> getPublisher(@PathVariable Long id) {
        return publisherRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/publishers")
    public ResponseEntity<Publisher> createPublisher(@RequestBody Publisher publisher) {
        return ResponseEntity.ok(publisherRepository.save(publisher));
    }
    
    @PutMapping("/publishers/{id}")
    public ResponseEntity<Publisher> updatePublisher(@PathVariable Long id, @RequestBody Publisher publisher) {
        if (!publisherRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        publisher.setId(id);
        return ResponseEntity.ok(publisherRepository.save(publisher));
    }
    
    @DeleteMapping("/publishers/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        if (!publisherRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        publisherRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Library Resources CRUD ===
    @GetMapping("/resources")
    public ResponseEntity<Page<LibraryResource>> getAllResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(libraryResourceRepository.findAll(pageable));
    }
    
    @GetMapping("/resources/{id}")
    public ResponseEntity<LibraryResource> getResource(@PathVariable Long id) {
        return libraryResourceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/resources")
    public ResponseEntity<LibraryResource> createResource(@RequestBody LibraryResource resource) {
        return ResponseEntity.ok(libraryResourceRepository.save(resource));
    }
    
    @PutMapping("/resources/{id}")
    public ResponseEntity<LibraryResource> updateResource(@PathVariable Long id, @RequestBody LibraryResource resource) {
        if (!libraryResourceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        resource.setId(id);
        return ResponseEntity.ok(libraryResourceRepository.save(resource));
    }
    
    @DeleteMapping("/resources/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        if (!libraryResourceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        libraryResourceRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Resource Items CRUD ===
    @GetMapping("/items")
    public ResponseEntity<Page<ResourceItem>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(resourceItemRepository.findAll(pageable));
    }
    
    @GetMapping("/items/{id}")
    public ResponseEntity<ResourceItem> getItem(@PathVariable Long id) {
        return resourceItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/items")
    public ResponseEntity<ResourceItem> createItem(@RequestBody ResourceItem item) {
        return ResponseEntity.ok(resourceItemRepository.save(item));
    }
    
    @PutMapping("/items/{id}")
    public ResponseEntity<ResourceItem> updateItem(@PathVariable Long id, @RequestBody ResourceItem item) {
        if (!resourceItemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        item.setId(id);
        return ResponseEntity.ok(resourceItemRepository.save(item));
    }
    
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (!resourceItemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        resourceItemRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Library Loans CRUD ===
    @GetMapping("/loans")
    public ResponseEntity<Page<LibraryLoan>> getAllLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(libraryLoanRepository.findAll(pageable));
    }
    
    @GetMapping("/loans/{id}")
    public ResponseEntity<LibraryLoan> getLoan(@PathVariable Long id) {
        return libraryLoanRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/loans")
    public ResponseEntity<LibraryLoan> createLoan(@RequestBody LibraryLoan loan) {
        return ResponseEntity.ok(libraryLoanRepository.save(loan));
    }
    
    @PutMapping("/loans/{id}")
    public ResponseEntity<LibraryLoan> updateLoan(@PathVariable Long id, @RequestBody LibraryLoan loan) {
        if (!libraryLoanRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        loan.setId(id);
        return ResponseEntity.ok(libraryLoanRepository.save(loan));
    }
    
    @DeleteMapping("/loans/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        if (!libraryLoanRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        libraryLoanRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Library Holds CRUD ===
    @GetMapping("/holds")
    public ResponseEntity<Page<LibraryHold>> getAllHolds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(libraryHoldRepository.findAll(pageable));
    }
    
    @GetMapping("/holds/{id}")
    public ResponseEntity<LibraryHold> getHold(@PathVariable Long id) {
        return libraryHoldRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/holds")
    public ResponseEntity<LibraryHold> createHold(@RequestBody LibraryHold hold) {
        return ResponseEntity.ok(libraryHoldRepository.save(hold));
    }
    
    @PutMapping("/holds/{id}")
    public ResponseEntity<LibraryHold> updateHold(@PathVariable Long id, @RequestBody LibraryHold hold) {
        if (!libraryHoldRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        hold.setId(id);
        return ResponseEntity.ok(libraryHoldRepository.save(hold));
    }
    
    @DeleteMapping("/holds/{id}")
    public ResponseEntity<Void> deleteHold(@PathVariable Long id) {
        if (!libraryHoldRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        libraryHoldRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Library Policies CRUD ===
    @GetMapping("/policies")
    public ResponseEntity<Page<LibraryPolicy>> getAllPolicies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(libraryPolicyRepository.findAll(pageable));
    }
    
    @GetMapping("/policies/{id}")
    public ResponseEntity<LibraryPolicy> getPolicy(@PathVariable Long id) {
        return libraryPolicyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/policies")
    public ResponseEntity<LibraryPolicy> createPolicy(@RequestBody LibraryPolicy policy) {
        return ResponseEntity.ok(libraryPolicyRepository.save(policy));
    }
    
    @PutMapping("/policies/{id}")
    public ResponseEntity<LibraryPolicy> updatePolicy(@PathVariable Long id, @RequestBody LibraryPolicy policy) {
        if (!libraryPolicyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        policy.setId(id);
        return ResponseEntity.ok(libraryPolicyRepository.save(policy));
    }
    
    @DeleteMapping("/policies/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        if (!libraryPolicyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        libraryPolicyRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Library Purchase Requests CRUD ===
    @GetMapping("/purchase-requests")
    public ResponseEntity<Page<LibraryPurchaseRequest>> getAllPurchaseRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(libraryPurchaseRequestRepository.findAll(pageable));
    }
    
    @GetMapping("/purchase-requests/{id}")
    public ResponseEntity<LibraryPurchaseRequest> getPurchaseRequest(@PathVariable Long id) {
        return libraryPurchaseRequestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/purchase-requests")
    public ResponseEntity<LibraryPurchaseRequest> createPurchaseRequest(@RequestBody LibraryPurchaseRequest request) {
        return ResponseEntity.ok(libraryPurchaseRequestRepository.save(request));
    }
    
    @PutMapping("/purchase-requests/{id}")
    public ResponseEntity<LibraryPurchaseRequest> updatePurchaseRequest(@PathVariable Long id, @RequestBody LibraryPurchaseRequest request) {
        if (!libraryPurchaseRequestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        request.setId(id);
        return ResponseEntity.ok(libraryPurchaseRequestRepository.save(request));
    }
    
    @DeleteMapping("/purchase-requests/{id}")
    public ResponseEntity<Void> deletePurchaseRequest(@PathVariable Long id) {
        if (!libraryPurchaseRequestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        libraryPurchaseRequestRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // === Library Purchase Orders CRUD ===
    @GetMapping("/pos")
    public ResponseEntity<Page<LibraryPO>> getAllPOs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(libraryPORepository.findAll(pageable));
    }
    
    @GetMapping("/pos/{id}")
    public ResponseEntity<LibraryPO> getPO(@PathVariable Long id) {
        return libraryPORepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/pos")
    public ResponseEntity<LibraryPO> createPO(@RequestBody LibraryPO po) {
        return ResponseEntity.ok(libraryPORepository.save(po));
    }
    
    @PutMapping("/pos/{id}")
    public ResponseEntity<LibraryPO> updatePO(@PathVariable Long id, @RequestBody LibraryPO po) {
        if (!libraryPORepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        po.setId(id);
        return ResponseEntity.ok(libraryPORepository.save(po));
    }
    
    @DeleteMapping("/pos/{id}")
    public ResponseEntity<Void> deletePO(@PathVariable Long id) {
        if (!libraryPORepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        libraryPORepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
