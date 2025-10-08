package com.spring.jpa.envers.controller;

import com.spring.jpa.envers.entity.Book;
import com.spring.jpa.envers.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SpringDataEnversApplication
{

    @Autowired
    private BookRepository repository;


    @PostMapping("/saveBook")
    public Book saveBook(@RequestBody Book book) {
        return repository.save(book);
    }

    @PutMapping("/update/{id}/{pages}")
    public String updateBook(@PathVariable int id, @PathVariable int pages) {
        Book book = repository.findById(id).get();
        book.setPages(pages);
        repository.save(book);
        return "nook updated";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable int id) {
        repository.deleteById(id);
        return "book deleted";
    }
    @GetMapping("/getInfo/{id}")
    public void getInfo(@PathVariable  int id){
        System.out.println(repository.findLastChangeRevision(id));
    }
}
