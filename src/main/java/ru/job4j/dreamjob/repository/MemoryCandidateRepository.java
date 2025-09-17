package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Ivan Ivanov", "Intern Java Developer"));
        save(new Candidate(0, "Petr Petrov", "Junior Java Developer"));
        save(new Candidate(0, "Stepan Stepanov", "Junior+ Java Developer"));
        save(new Candidate(0, "Anna Ivanova", "Middle Java Developer"));
        save(new Candidate(0, "Mariya Petrova", "Middle+ Java Developer"));
        save(new Candidate(0, "Elena Stepanova", "Senior Java Developer"));
    }

    @Override
    public void save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(oldCandidate.getId(), candidate.getName(), candidate.getDescription())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
