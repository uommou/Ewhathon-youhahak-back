package com.ewhathon.notegather.service;

import com.ewhathon.notegather.domain.entity.Lecture;
import com.ewhathon.notegather.domain.entity.Note;
import com.ewhathon.notegather.domain.repository.LectureRepository;
import com.ewhathon.notegather.domain.repository.NoteRepository;
import com.ewhathon.notegather.web.dto.NoteListResponseDto;
import com.ewhathon.notegather.web.dto.NoteRequestDto;
import com.ewhathon.notegather.web.dto.NoteResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final LectureRepository lectureRepository;

    public NoteResponseDto createNote(NoteRequestDto noteRequestDto){
        Optional<Lecture> lecture = lectureRepository.findLectureByNameAndProfessor(noteRequestDto.getLectureName(),noteRequestDto.getProfessorName());
        if(lecture.isEmpty()){
            lecture = Optional.ofNullable(Lecture.builder()
                    .name(noteRequestDto.getLectureName())
                    .professor(noteRequestDto.getProfessorName())
                    .build());
            lectureRepository.save(lecture.get());
        }

        Note note = Note.builder()
                .title(noteRequestDto.getTitle())
                .content(noteRequestDto.getContent())
                .lecture(lecture.get())
                .createdDate(LocalDateTime.now())
                .build();

        noteRepository.save(note);

        return new NoteResponseDto(note.getId(),note.getTitle(), note.getContent(), note.getCreatedDate(), note.getLecture().getName(), note.getLecture().getProfessor());
    }

    public List<NoteListResponseDto> getNotes(){
        List<NoteListResponseDto> noteListResponseDtos = new ArrayList<>();
        for(Note note : noteRepository.findAll()){
            noteListResponseDtos.add(new NoteListResponseDto(note.getId(), note.getTitle(), note.getLecture().getName(), note.getLecture().getProfessor()));
        }

        return noteListResponseDtos;
    }

    public NoteResponseDto getNote(Long noteId) throws Exception{
        return new NoteResponseDto(noteRepository.findById(noteId).orElseThrow(()-> new Exception("노트를 찾을 수 없습니다.")));
    }
}
