package com.novokren.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.novokren.exceptions.AlreadyUsedNameException;
import com.novokren.exceptions.RegionNotFoundException;
import com.novokren.model.Region;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RegionRepository {
    private final String jsonRegionsPath = "src/main/resources/files/regions.json";

    public RegionRepository() {
        repositoryInitialization();
    }

    public List<Region> getAll() {
        List<Region> regions = new ArrayList<>();
        Type listType = new TypeToken<List<Region>>() {
        }.getType();

        try (FileReader fileReader = new FileReader(jsonRegionsPath)) {
            if (fileReader.ready()) {
                regions = new Gson().fromJson(fileReader, listType);
            }
        } catch (FileNotFoundException e) {
            System.out.println("regions.json file not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return regions;
    }

    public Region getById(long id) throws RegionNotFoundException {
        List<Region> regions = getAll();
        boolean isContains = regions.stream()
                .anyMatch(region -> (region.getId() == id));

        if (isContains) {
            Optional<Region> result = regions.stream()
                    .filter(person -> person.getId() == id)
                    .findFirst();
            return result.get();
        }

        throw new RegionNotFoundException("Region with given id not found id: " + id, id);
    }

    public Region save(Region region) {
        List<Region> regions = getAll();
        boolean isContains = regions.stream()
                .anyMatch(reg -> reg.getName().equalsIgnoreCase(region.getName()));

        if (isContains) {
            Optional<Region> result = regions.stream()
                    .filter(reg -> reg.getName().equalsIgnoreCase(region.getName()))
                    .findFirst();
            return result.get();
        }
        if (regions.size() == 0) {
            region.setId(1);
        } else {
            int index = regions.size() - 1;
            region.setId((regions.get(index).getId()) + 1);
        }
        regions.add(region);
        saveListInJs(regions);
        return region;
    }

    public Region update(Region region) throws RegionNotFoundException, AlreadyUsedNameException {
        List<Region> regions = getAll();
        boolean isContains = regions.stream()
                .anyMatch(reg -> reg.getId() == reg.getId());

        if(isContains) {
            boolean isNameFree = regions.stream()
                    .filter(reg -> reg.getId() != region.getId())
                    .noneMatch(reg -> reg.getName().equalsIgnoreCase(region.getName()));
            if (isNameFree) {
                Region updatedRegion = updateRegionWithFreeName(regions, region);
                return updatedRegion;
            } else {
                Region regionWithSameName = regions.stream()
                        .filter(reg -> reg.getName().equalsIgnoreCase(region.getName()))
                        .findFirst()
                        .get();

                throw new AlreadyUsedNameException("Region name: " + region.getName()
                        + " already in use by Region with id: " + regionWithSameName.getId());
            }

        } else {
            throw new RegionNotFoundException("Region for update not found", region.getId());
        }

    }

    public void delete(Region region) throws RegionNotFoundException {
        List<Region> regions = getAll();
        boolean isContains = regions.stream()
                .anyMatch(reg -> reg.equals(region));

        if (isContains) {
            List<Region> updatedRegions = regions.stream()
                    .filter(reg -> !reg.equals(region))
                    .collect(Collectors.toList());
            saveListInJs(updatedRegions);
        } else {
            throw new RegionNotFoundException("Region to delete not found", region.getId());
        }

    }

    private void saveListInJs(List<Region> regions) {
        String jsonStr = new Gson().toJson(regions);

        try (FileWriter writer = new FileWriter(jsonRegionsPath)) {
            writer.write(jsonStr);
        } catch (FileNotFoundException e) {
            System.out.println("RegionsJson file not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Region updateRegionWithFreeName(List<Region> regions, Region region) {
        Region regionForUpdate = regions.stream()
                .filter(reg -> reg.getId() == region.getId())
                .findFirst()
                .get();

        regionForUpdate.setName(region.getName());
        saveListInJs(regions);

        return regionForUpdate;
    }
    private void repositoryInitialization() {
        Path path = Paths.get(jsonRegionsPath);

        if (!Files.exists(path)) {
            String jsonStr = new Gson().toJson(new ArrayList<>());
            try (FileWriter writer = new FileWriter(path.toString())) {
                writer.write(jsonStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
