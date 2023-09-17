package com.example.demo.dao;

import com.example.demo.model.Student;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Repository
public class StudentDao {
    private List<Student> students = new ArrayList<>();
    private Flux<Student> studentFlux = Flux.empty();

    public Flux<Student> getStudentFlux(){return studentFlux;}
    public List<Student> getStudents(){
        return students;
    }


    public Student addStudent(String name, String email){
        UUID id = UUID.randomUUID();
        Student student = new Student(id, name, email);
        students.add(student);
        return student;
    }
    public Mono<Student> addStudentMono(String name, String email){
        UUID id = UUID.randomUUID();
        Student student = new Student(id, name, email);
        studentFlux = studentFlux.concatWithValues(student);
        return Mono.just(student);
    }


    public Student updateStudent(Student updatedStudent) {
        for (Student student : students) {
            if (student.getId().equals(updatedStudent.getId())) {
                student.setName(updatedStudent.getName());
                student.setEmail(updatedStudent.getEmail());
                return student;
            }
        }
        throw new IllegalArgumentException("Student not found with ID: " + updatedStudent.getId());
    }
    public Flux<Student> updateStudentMono(Student updatedStudent) {
        return studentFlux
                .filter(student -> student.getId().equals(updatedStudent.getId()))
                .flatMap(student -> {
                    student.setName(updatedStudent.getName());
                    student.setEmail(updatedStudent.getEmail());
                    return Mono.just(student);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Student not found with ID: " + updatedStudent.getId())));
    }

    public void deleteStudent(UUID studentId) {
        Iterator<Student> iterator = students.iterator();
        while (iterator.hasNext()) {
            Student student = iterator.next();
            if (student.getId().equals(studentId)) {
                iterator.remove();
                return;
            }
        }
        throw new IllegalArgumentException("Student not found with ID: " + studentId);
    }
    public Mono<Void> deleteStudentMono(UUID studentId) {
        return studentFlux
                .filter(student -> student.getId().equals(studentId))
                .collectList()
                .flatMap(studentList -> {
                    if (!studentList.isEmpty()) {
                        studentFlux = studentFlux.filter(student -> !studentList.contains(student));
                        return Mono.empty();
                    } else {
                        return Mono.error(new IllegalArgumentException("Student not found with ID: " + studentId));
                    }
                });
    }

}
