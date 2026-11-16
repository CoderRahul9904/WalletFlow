package com.rsm.walletflow.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.rsm.walletflow.ai.rag.ingestion.DataReader;
import com.rsm.walletflow.ai.rag.ingestion.DataTransformer;
import com.rsm.walletflow.ai.rag.ingestion.VectorStoreWriter;
import com.rsm.walletflow.config.DataIngestionStatus;
import com.rsm.walletflow.dto.IngestionResponse;
import com.rsm.walletflow.entity.DataIngestion;
import com.rsm.walletflow.mapper.DataIngestionMapper;
import com.rsm.walletflow.repository.DataIngestionRepository;


@Service  
public class FinnyChatServiceImpl implements FinnyChatService {

    private final ChatClient finnyOpenAIChatClient;
    private final VectorStoreWriter vectorStoreWriter;
    private final DataTransformer dataTransformer;
    private final DataReader dataReader;
    private final DataIngestionRepository dataIngestionRepository;
    private final DataIngestionMapper dataIngestionMapper;

    public FinnyChatServiceImpl(@Qualifier("finnyOpenAIChatClient") ChatClient finnyOpenAIChatClient, VectorStoreWriter vectorStoreWriter, DataTransformer dataTransformer, DataReader dataReader, DataIngestionRepository dataIngestionRepository, DataIngestionMapper dataIngestionMapper) {
        this.finnyOpenAIChatClient = finnyOpenAIChatClient;
        this.vectorStoreWriter = vectorStoreWriter;
        this.dataTransformer = dataTransformer;
        this.dataReader = dataReader;
        this.dataIngestionRepository = dataIngestionRepository;
        this.dataIngestionMapper = dataIngestionMapper;
    }
    
    @Override
    public String chatWithFinny(String userMessage, Long id) {
       return finnyOpenAIChatClient.prompt(userMessage).advisors(a -> a.param(ChatMemory.CONVERSATION_ID, id)).call().content();
    }

    @Override
    public IngestionResponse ingestData(String message) {
        try {
            // Simulate data ingestion logic here
            // For example, you can call methods to read, transform, and store data
            // You can also use the 'param' parameter if needed for your ingestion logic

            // Simulating a successful ingestion process
            vectorStoreWriter.writeDocuments(dataTransformer.transformDocuments(dataReader.readDocuments()));
            return dataIngestionMapper.toDto(dataIngestionRepository.save(DataIngestion.builder().status(DataIngestionStatus.ACCEPTED).message(message).build()));
            
        } catch (Exception e) {
            // Handle exceptions and return an appropriate response
            return dataIngestionMapper.toDto(dataIngestionRepository.save(DataIngestion.builder().status(DataIngestionStatus.REJECTED).message(message).build()));
        }
    }

    
}
