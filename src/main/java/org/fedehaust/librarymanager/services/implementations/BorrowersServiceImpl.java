package org.fedehaust.librarymanager.services.implementations;

import org.fedehaust.librarymanager.dtos.BookBorrowedResponse;
import org.fedehaust.librarymanager.dtos.BorrowerRequest;
import org.fedehaust.librarymanager.dtos.BorrowerResponse;
import org.fedehaust.librarymanager.exceptions.InvalidEmailException;
import org.fedehaust.librarymanager.mappers.BookBorrowerMapper;
import org.fedehaust.librarymanager.mappers.BorrowersMapper;
import org.fedehaust.librarymanager.repositories.BookBorrowersRepository;
import org.fedehaust.librarymanager.repositories.BorrowersRepository;
import org.fedehaust.librarymanager.services.interfaces.BookBorrowersServiceHelper;
import org.fedehaust.librarymanager.services.interfaces.BorrowersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class BorrowersServiceImpl implements BorrowersService {

    private final BorrowersRepository borrowersRepository;
    private final BookBorrowersRepository bookBorrowersRepository;
    private final BookBorrowersServiceHelper bookBorrowersServiceHelper;

    public BorrowersServiceImpl(
            BorrowersRepository borrowersRepository,
            BookBorrowersRepository bookBorrowersRepository,
            BookBorrowersServiceHelper bookBorrowersServiceHelper) {
        this.borrowersRepository = borrowersRepository;
        this.bookBorrowersRepository = bookBorrowersRepository;
        this.bookBorrowersServiceHelper = bookBorrowersServiceHelper;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<BorrowerResponse> findAllBorrowers(boolean loadBooks) {
        return BorrowersMapper.borrowersToDtoList(borrowersRepository.findAll(), loadBooks);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public BorrowerResponse findBorrowerById(Long id, boolean loadBooks) {
        return BorrowersMapper.borrowerToDto(bookBorrowersServiceHelper.getBorrower(id), loadBooks);
    }

    @Override
    public BorrowerResponse createBorrower(BorrowerRequest borrowerRequest, boolean loadBooks) {
        String email = borrowerRequest.email();
        if(EmailValidatorHelper.isInvalid(email))
            throw new InvalidEmailException(email);

        var borrower = BorrowersMapper.dtoToEntity(borrowerRequest);
        return BorrowersMapper.borrowerToDto(borrowersRepository.save(borrower), loadBooks);
    }

    @Override
    public List<BookBorrowedResponse> findBorrowedBooksByBorrower(Long id) {
        return BookBorrowerMapper.bookBorrowersToDtoList(bookBorrowersRepository
                .getByBorrowerId(id)
                .orElse(Collections.emptyList()));
    }
}
