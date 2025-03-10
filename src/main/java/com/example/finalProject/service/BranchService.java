package com.example.finalProject.service;

import com.example.finalProject.dto.BranchDTO;
import com.example.finalProject.model.Branch;
import com.example.finalProject.repository.BranchRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BranchService {

    ModelMapper mapper =new ModelMapper();

    @Autowired
    private BranchRepository branchRepository;

    public Page<BranchDTO> getAllBranch(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);  // Multiple fields can be added here
        Pageable pageable = PageRequest.of(page, size, sort);
        return branchRepository.findAll(pageable).map(branch -> mapper.map(branch, BranchDTO.class));
    }

    public BranchDTO createBranch(BranchDTO branchDTO) {
        Branch branch = mapper.map(branchDTO,Branch.class);
        Branch newBranch = branchRepository.save(branch);
        return mapper.map(newBranch,BranchDTO.class);
    }

    public BranchDTO getById(long id) {
        return mapper.map(branchRepository.findById(id),BranchDTO.class);
    }

    @Transactional
    public BranchDTO updateBranch(long id, BranchDTO branchDTO) {
        Branch branch = branchRepository.findById(id).orElse(null);
        if(branch!=null){
            branch.setName(branchDTO.getName());
            branch.setAddress(branchDTO.getAddress());
            branch.setPhone(branch.getPhone());
            return mapper.map(branch,BranchDTO.class);
        }
        else
            return null;
    }

    public boolean deleteBranch(long id) {
        boolean isPresent = branchRepository.existsById(id);
        if(isPresent){
            branchRepository.deleteById(id);
            return true;
        }
        else
            return false;
    }

    public Branch getByName(String name) {
        return branchRepository.findByName(name);
    }

    public Object getAll() {
        return branchRepository.findAll();
    }
}
