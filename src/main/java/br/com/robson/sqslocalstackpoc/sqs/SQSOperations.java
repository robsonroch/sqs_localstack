package br.com.robson.sqslocalstackpoc.sqs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

@Service
public class SQSOperations {
		
	@Autowired
    private AmazonSQS sqsClient;

    public String createQueue(String queueName) {
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
        return sqsClient.createQueue(createQueueRequest).getQueueUrl();
    }

    public void sendMessage(String queueUrl, String message) {
        SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, message);
        sqsClient.sendMessage(sendMessageRequest);
    }

    public List<Message> receiveMessages(String queueUrl, int maxNumberOfMessages) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(maxNumberOfMessages);

        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).getMessages();
        return messages;
    }
}
