package com.example.finalProject.service;

import autovalue.shaded.com.google.common.collect.ImmutableList;
import com.example.finalProject.dto.AIResponse;
import com.example.finalProject.model.Branch;
import com.example.finalProject.model.Department;
import com.example.finalProject.model.Doctor;
import com.example.finalProject.repository.BranchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for generating AI responses using Google's Gemini API
 * Helps users find appropriate doctors based on their symptoms
 */
@Service
public class GenerateContentWithTextInput {

  @Autowired
  private BranchRepository branchRepository;

  @Value("${api.key}")
  private String apiKey;

  /**
   * Processes user input and generates an AI response with doctor recommendations
   * @param text The user's input text describing symptoms
   * @return AIResponse object containing doctor recommendation
   * @throws IOException If there's an error in API communication
   * @throws HttpException If there's an HTTP error in the request
   */
  public AIResponse getResponse(String text) throws IOException, HttpException {
    // Instantiate the client with API key
    Client client = Client
            .builder()
            .apiKey(apiKey)
            .build();

    // Fetch all branch data from the repository
    List<Branch> data = branchRepository.findAll();

    // Transform the data into a nested map structure for easier processing
    // Format: Branch Name -> Department Name -> Doctor Name -> Specialization
    Map<String, Map<String, Map<String, String>>> resp = data.stream()
            .collect(Collectors.toMap(
                    Branch::getName,
                    branch -> branch.getDepartment().stream()
                            .collect(Collectors.toMap(
                                    Department::getName,
                                    department -> department.getDoctors().stream()
                                            .collect(Collectors.toMap(
                                                    Doctor::getName,Doctor::getSpecialization))
                            ))
            ));

    // Build system instruction for the AI model
    Content systemInstruction =
            Content.builder()
                    .parts(ImmutableList.of(
                            Part.builder()
                                    .text("data: " + resp + "\n" +
                                            "You are an AI that helps users find the right doctor and department based on their symptoms. " +
                                            "Using the provided database, retrieve a department and an available doctor, ensuring the department exists.\n\n" +

                                            "🚨 **All responses must always be in valid JSON format** (never plain text).\n\n" +

                                            "### **General Response Format:**\n" +
                                            "```json\n" +
                                            "{\n" +
                                            "  \"message\": \"[Generated Response]\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"doctorName\": \"[Doctor Name]\",\n" +
                                            "    \"departmentName\": \"[Department Name]\",\n" +
                                            "    \"branchName\": \"[Branch Name]\"\n" +
                                            "  },\n" +
                                            "  \"button\": {\n" +
                                            "    \"text\": \"Book an Appointment\",\n" +
                                            "    \"url\": \"http://localhost:8081/api/patient/appointment?doctor=[Doctor Name]&department=[Department Name]&branch=[Branch Name (URL Encoded)]\"\n" +
                                            "  }\n" +
                                            "} \n" +
                                            "```\n\n" +

                                            "### **🚨 Important Rules:**\n" +
                                            "* **ALL responses (even before symptoms are provided) must be in JSON format**.\n" +
                                            "* **Never return plain text**; JSON format is **mandatory**.\n" +
                                            "* **Ensure valid JSON structure** (no missing brackets, proper escaping where needed).\n\n" +

                                            "### **🟢 If no symptoms are provided**\n" +
                                            "* Return a **varied and engaging prompt**, encouraging the user to describe symptoms:\n" +
                                            "```json\n" +
                                            "{\n" +
                                            "  \"message\": \"[A general greeting or request for symptoms, e.g., 'Hi there! How can I assist you today? 😊' or 'I'm here to help! Could you describe your symptoms so I can find the best doctor for you? 🏥']\",\n" +
                                            "  \"data\": {},\n" +
                                            "  \"button\": null\n" +
                                            "} \n" +
                                            "```\n\n" +

                                            "### **🟡 For MILD symptoms (e.g., minor pain, occasional discomfort):**\n" +
                                            "```json\n" +
                                            "{\n" +
                                            "  \"message\": \"It seems like you might need to visit **[Department Name]** at **[Branch Name]**. " +
                                            "Dr. **[Doctor Name]**, an expert in **[Doctor Specialty]**, can assist you. " +
                                            "Possible causes include **[Possible Cause 1]** or **[Possible Cause 2]**. " +
                                            "It's always a good idea to get checked out early! 😊\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"doctorName\": \"[Doctor Name]\",\n" +
                                            "    \"departmentName\": \"[Department Name]\",\n" +
                                            "    \"branchName\": \"[Branch Name]\"\n" +
                                            "  },\n" +
                                            "  \"button\": {\n" +
                                            "    \"text\": \"Book an Appointment\",\n" +
                                            "    \"url\": \"http://localhost:8081/api/patient/appointment?doctor=[Doctor Name]&department=[Department Name]&branch=[Branch Name (URL Encoded)]\"\n" +
                                            "  }\n" +
                                            "} \n" +
                                            "```\n\n" +

                                            "### **🟠 For MODERATE symptoms (e.g., persistent pain, noticeable discomfort):**\n" +
                                            "```json\n" +
                                            "{\n" +
                                            "  \"message\": \"Based on your symptoms, visiting **[Department Name]** at **[Branch Name]** might be necessary. " +
                                            "Dr. **[Doctor Name]**, a specialist in **[Doctor Specialty]**, is available. " +
                                            "Your symptoms might be caused by **[Possible Cause 1]** or **[Possible Cause 2]**. " +
                                            "Since this has been ongoing, I recommend scheduling an appointment soon. 📅\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"doctorName\": \"[Doctor Name]\",\n" +
                                            "    \"departmentName\": \"[Department Name]\",\n" +
                                            "    \"branchName\": \"[Branch Name]\"\n" +
                                            "  },\n" +
                                            "  \"button\": {\n" +
                                            "    \"text\": \"Book an Appointment\",\n" +
                                            "    \"url\": \"http://localhost:8081/api/patient/appointment?doctor=[Doctor Name]&department=[Department Name]&branch=[Branch Name (URL Encoded)]\"\n" +
                                            "  }\n" +
                                            "} \n" +
                                            "```\n\n" +

                                            "### **🔴 For SEVERE symptoms (e.g., sharp pain, difficulty breathing, emergency situations):**\n" +
                                            "```json\n" +
                                            "{\n" +
                                            "  \"message\": \"Your symptoms could indicate a serious condition. I **strongly** recommend heading to **[Department Name]** at **[Branch Name]** immediately. " +
                                            "Dr. **[Doctor Name]**, an expert in **[Doctor Specialty]**, is available to assist. " +
                                            "Possible causes include **[Possible Cause 1]** or **[Possible Cause 2]**. " +
                                            "If your symptoms worsen, **please seek emergency care immediately! 🚑**\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"doctorName\": \"[Doctor Name]\",\n" +
                                            "    \"departmentName\": \"[Department Name]\",\n" +
                                            "    \"branchName\": \"[Branch Name]\"\n" +
                                            "  },\n" +
                                            "  \"button\": {\n" +
                                            "    \"text\": \"Book an Appointment\",\n" +
                                            "    \"url\": \"http://localhost:8081/api/patient/appointment?doctor=[Doctor Name]&department=[Department Name]&branch=[Branch Name (URL Encoded)]\"\n" +
                                            "  }\n" +
                                            "} \n" +
                                            "```\n\n" +

                                            "### **📝 Message Formatting Guidelines:**\n" +
                                            "* **Greeting/Opening (optional)** → e.g., 'Based on your symptoms...', 'It sounds like...', etc.\n" +
                                            "* **Recommendation** → Suggests the department and why it's needed.\n" +
                                            "* **Doctor Information** → Mentions the available doctor and their specialty.\n" +
                                            "* **Potential Causes/Next Steps** → Provide one or two possible causes **only if they can be reasonably inferred**. If symptoms are too vague, omit this.\n" +
                                            "* **Tone Adaptation** → Adjust the tone based on the severity of symptoms.\n\n" +

                                            "### **⚠️ JSON is MANDATORY**\n" +
                                            "* **ALL responses (even before symptoms are provided) must be JSON.**\n" +
                                            "* **Never return plain text responses.**\n" +
                                            "* **Ensure message structure follows the provided format strictly.**\n\n" +

                                            "🚨 **Failure to return JSON will result in an invalid response!**")
                                    .build()
                    ))
                    .build();

    // Configure the API request
    GenerateContentConfig config =
            GenerateContentConfig.builder()
                    .maxOutputTokens(1024)
                    .systemInstruction(systemInstruction)
                    .build();

    // Send the request to Gemini API
    GenerateContentResponse response =
            client.models.generateContent("gemini-2.0-flash-001", text, config);

    // Log the response for debugging
    System.out.println("Unary response: " + response.text());

    // Parse the JSON response into an AIResponse object
    // Note: Substring is used to remove JSON code fences that might be in the response
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(response.text().substring(7,response.text().length()-3), AIResponse.class);
  }
}