package com.example.demo.controller;

import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
public class StudentController {
    StudentService studentService;
    StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @GetMapping(value = "/students")
    List<Student> students(){
        return studentService.students();
    }

    @PostMapping("/student")
    Student addStudent(@RequestBody Student student){
        return studentService.addStudent(student.getName(), student.getEmail());
    }

    @PutMapping("/student")
    public ResponseEntity<?> updateBook(@RequestBody Student student) {
        try {
            Student updatedStudent = studentService.updateStudent(student);
            return ResponseEntity.ok(updatedStudent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID studentId) {
        try {
            studentService.deleteStudent(studentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


   @GetMapping(value = "/rstudents", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Student> studentFlux(){
        return studentService.studentFlux();
    }
    @PostMapping("/rstudent")
    Mono<Student> addStudentMono(@RequestBody Student student){
        return studentService.addStudentMono(student.getName(), student.getEmail());
    }
    @PutMapping("/rstudent")
    Flux<Student> updateStudentMono(@RequestBody Student student) {
        return studentService.updateStudentMono(student);

    }
    @DeleteMapping("/r/{studentId}")
    Mono<Void> deleteStudentMono(@PathVariable UUID studentId) {
            return studentService.deleteStudentMono(studentId);
    }

}
