package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryVacancyRepository implements VacancyRepository {

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final ConcurrentHashMap<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    public MemoryVacancyRepository() {
        save(new Vacancy(1, "Intern Java Developer", "description1", true, 1));
        save(new Vacancy(2, "Junior Java Developer", "description2", true, 2));
        save(new Vacancy(3, "Junior+ Java Developer", "description3", true, 3));
        save(new Vacancy(4, "Middle Java Developer", "description4", true, 1));
        save(new Vacancy(5, "Middle+ Java Developer", "description5", true, 2));
        save(new Vacancy(6, "Senior Java Developer", "description6", true, 3));
    }

    @Override
    public void save(Vacancy vacancy) {
        vacancy.setId(nextId.incrementAndGet());
        vacancies.put(vacancy.getId(), vacancy);
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(), (id, oldVacancy) -> new Vacancy(oldVacancy.getId(), vacancy.getTitle(), vacancy.getDescription(), vacancy.getVisible(), vacancy.getCityId())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}