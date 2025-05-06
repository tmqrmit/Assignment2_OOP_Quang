package app.util;

import app.model.Academic;
import app.model.Person;
import app.model.Professional;
import app.model.Student;
import app.service.AcademicService;
import app.service.StudentService;
import app.service.ProfessionalService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

public class FindPerson {

    private AcademicService academicService;
    private StudentService studentService;
    private ProfessionalService professionalService;

    public FindPerson() {
        EntityManager entityManager = Persistence
                .createEntityManagerFactory("your-persistence-unit")
                .createEntityManager();
        academicService = new AcademicService(entityManager);
        studentService = new StudentService(entityManager);
        professionalService = new ProfessionalService(entityManager);
    }
    public Person findPersonById(String personId) {
        Academic foundAcademic = this.academicService.findByPersonId(personId);
        if (foundAcademic != null) {
            return foundAcademic;
        }
        Student foundStudent = this.studentService.findByPersonId(personId);
        if (foundStudent != null) {
            return foundStudent;
        }
        Professional foundProfessional = this.professionalService.findByPersonId(personId);
        if (foundProfessional != null) {
            return foundProfessional;
        }
        return null;
    }

}
