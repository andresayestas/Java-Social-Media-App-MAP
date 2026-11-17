package repository;

import domain.Entity;
import validators.ValidationException;
import validators.Validator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.io.*;
import java.util.stream.StreamSupport;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E>{

    protected String fileName;

    public AbstractFileRepository(Validator<E> validator, String fileName){
        super(validator);
        this.fileName = fileName;

    }

    protected abstract E createEntity(String line);
    protected abstract String saveEntity(E entity);

    protected void loadData(){
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            System.out.println("Fisier " + fileName + " nu exista inca");
            return;
        }

        try {

            List<String> allLines = Files.readAllLines(path);

            IntStream.range(0, allLines.size())
                    .forEach(index -> {
                        String line = allLines.get(index);
                        if (!line.trim().isEmpty()) {
                            try {
                                E entity = createEntity(line);
                                super.save(entity);
                            } catch (Exception e){

                                System.out.println("Linie ignorata (" + (index + 1) + "): " + e.getMessage());
                            }
                        }
                    });
        } catch (IOException e) {
            throw new RepositoryException("Eror cand se a citit fisier " + fileName, e);
        } catch (ValidationException e) {
            throw new RepositoryException("Error cand se a validat datele de fisier", e);
        }
    }



    protected void writeToFile() {
        try {
            Path path = Paths.get(fileName);

            List<String> lines = StreamSupport.stream(super.findAll().spliterator(), false)
                    .map(this::saveEntity)
                    .collect(Collectors.toList());

            Files.write(path, lines);
        } catch (IOException e) {
            throw new RepositoryException("Error cand se a scris in fisier " + fileName, e);
        }
    }

    @Override
    public Optional<E> save(E entity) throws ValidationException{
        Optional<E> result = super.save(entity);
        if (result.isEmpty()) {
            writeToFile();
        }
        return result;
    }

    @Override
    public Optional<E> delete(ID id){
        Optional<E> result = super.delete(id);
        if (result.isPresent()) {
            writeToFile();
        }
        return result;
    }

    @Override
    public Optional<E> update(E entity) throws ValidationException{
        Optional<E> result = super.update(entity);
        if (result.isEmpty()){
            writeToFile();
        }
        return result;
    }
}