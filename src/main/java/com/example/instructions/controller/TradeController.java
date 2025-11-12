package com.example.instructions.controller;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.service.TradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/instructions")
public class TradeController {

    private final ObjectMapper objectMapper;
    private final TradeService tradeService;

    public TradeController(ObjectMapper objectMapper, TradeService tradeService) {
        this.objectMapper = objectMapper;
        this.tradeService = tradeService;
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> handleInstructionsFileUpload(@RequestParam("file") MultipartFile file,
                                                               @RequestParam("type") String type) {
        try {
            String content = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            if ("json".equalsIgnoreCase(type)) {
                tradeService.handleCanonicalTradeInstruction(parseJson(file));

                return ResponseEntity.ok("Received JSON:\n" + content);
            } else if ("csv".equalsIgnoreCase(type)) {
                return ResponseEntity.ok("Received CSV:\n" + content);
            } else {
                return ResponseEntity.badRequest()
                        .body("Wrong file type. Use type=json or type=csv");
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Can't process file: " + e.getMessage());
        }
    }

    private CanonicalTrade parseJson(MultipartFile file) throws IOException {
        return objectMapper.readValue(file.getInputStream(), CanonicalTrade.class);
    }

    // untested, out of time
    private CanonicalTrade parseCsv(MultipartFile file) throws IOException {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            CSVRecord record = parser.iterator().next(); // tylko jeden wiersz

            CanonicalTrade trade = new CanonicalTrade();

            trade.setInstructionId(record.get("instructionId"));
            trade.setTradeDate(LocalDate.parse(record.get("tradeDate")));
            trade.setSettlementDate(LocalDate.parse(record.get("settlementDate")));
            trade.setSourceSystem(record.get("sourceSystem"));

            CanonicalTrade.Instrument instrument = new CanonicalTrade.Instrument();
            instrument.setSymbol(record.get("symbol"));
            instrument.setIsin(record.get("isin"));
            instrument.setInstrumentType(record.get("instrumentType"));
            trade.setInstrument(instrument);

            CanonicalTrade.Transaction tx = new CanonicalTrade.Transaction();
            tx.setSide(record.get("side"));
            tx.setQuantity(Double.parseDouble(record.get("quantity")));
            tx.setPrice(Double.parseDouble(record.get("price")));
            tx.setCurrency(record.get("currency"));
            trade.setTransaction(tx);

            return trade;
        }
    }
}
