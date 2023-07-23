package br.com.robson.sqslocalstackpoc.transportlayers.openapi.api.impl;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.robson.sqslocalstackpoc.sqs.SQSOperations;
import br.com.robson.sqslocalstackpoc.transportlayers.openapi.api.ProdutosApi;
import br.com.robson.sqslocalstackpoc.transportlayers.openapi.model.ProdutoRequest;
import br.com.robson.sqslocalstackpoc.transportlayers.openapi.model.ProdutoResponse;

@RestController
@RequestMapping
public class ProdutosApiImpl implements ProdutosApi{
	
	private final SQSOperations sqsOperations;
	
	private final ObjectMapper objectMapper;
	
	private String queueUrl;
	
	public ProdutosApiImpl(SQSOperations sqsOperations, ObjectMapper objectMapper) {
		this.sqsOperations = sqsOperations;
		this.objectMapper = objectMapper;
	}
	
    public ResponseEntity<List<ProdutoResponse>> produtosGet() {   	
    	
    	
    	List<Message> receiveMessages = sqsOperations.receiveMessages(queueUrl, 10);
    	
    	List<ProdutoResponse> listResponse = new ArrayList<>();
    	
    	for(Message msg: receiveMessages) {
    		String messageBody = msg.getBody();
            try {
            	ProdutoResponse response = objectMapper.readValue(messageBody, ProdutoResponse.class);

            	listResponse.add(response);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("deu ruim ao receber mensagem");
            }
            
            sqsOperations.deleteMessage(queueUrl, msg);
    	}
    	
        return ResponseEntity.ok(listResponse);

    }
	
	@Override
    public ResponseEntity<Void> produtosPost(@Valid @RequestBody ProdutoRequest produtoRequest) {
		
        try {
            String messageBody = objectMapper.writeValueAsString(produtoRequest);
            
            if(queueUrl == null) {
            	queueUrl = sqsOperations.createQueue("my-queue");            	
            }
            
            sqsOperations.sendMessage(queueUrl, messageBody);

        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            System.out.println("deu ruim ao enviar mensagem");
        }
            	
        return ResponseEntity.noContent().build();

    }

}
