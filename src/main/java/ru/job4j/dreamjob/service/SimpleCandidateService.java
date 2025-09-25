package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleCandidateService implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final FileService fileService;

    public SimpleCandidateService(CandidateRepository sql2oCandidateRepository, FileService fileService) {
        this.candidateRepository = sql2oCandidateRepository;
        this.fileService = fileService;
    }

    @Override
    public void save(Candidate candidate, FileDto image) {
        saveNewFile(candidate, image);
        candidateRepository.save(candidate);
    }

    @Override
    public boolean deleteById(int id) {
        var candidateOptional = findById(id);
        if (candidateOptional.isEmpty()) {
            return false;
        }
        var candidate = candidateOptional.get();
        candidateRepository.deleteById(id);

        Optional.ofNullable(candidate.getFileId())
                .ifPresent(fileService::deleteById);

        return true;
    }

    @Override
    public boolean update(Candidate candidate, FileDto image) {
        var isNewFileEmpty = image.getContent().length == 0;
        if (isNewFileEmpty) {
            return candidateRepository.update(candidate);
        }

        Integer oldFileId = candidate.getFileId();
        saveNewFile(candidate, image);
        var isUpdated = candidateRepository.update(candidate);

        if (isUpdated && oldFileId != null) {
            fileService.deleteById(oldFileId);
        }

        return isUpdated;
    }

    private void saveNewFile(Candidate candidate, FileDto image) {
        var file = fileService.save(image);
        candidate.setFileId(file.getId());
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }
}