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
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
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
                tradeService.handleCanonicalTradeInstruction(parseCsv(file));

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

    private CanonicalTrade parseCsv(MultipartFile file) throws IOException {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            CSVRecord record = parser.iterator().next(); // one file - one trade

            CanonicalTrade trade = new CanonicalTrade();
            trade.setInstructionId(record.get("instructionId"));
            trade.setSourceSystem(record.get("sourceSystem"));

            // --- Instrument ---
            CanonicalTrade.Instrument instrument = new CanonicalTrade.Instrument();
            instrument.setSymbol(record.get("instrument.symbol"));
            instrument.setIsin(record.get("instrument.isin"));
            instrument.setInstrumentType(record.get("instrument.instrumentType"));
            trade.setInstrument(instrument);

            // --- Trader ---
            CanonicalTrade.Trader trader = new CanonicalTrade.Trader();
            trader.setId(record.get("trader.id"));
            trader.setName(record.get("trader.name"));
            trader.setAccount(record.get("trader.account"));
            trader.setSecurity(record.get("trader.security"));
            trade.setTrader(trader);

            // --- Transaction ---
            CanonicalTrade.Transaction tx = new CanonicalTrade.Transaction();
            tx.setSide(record.get("transaction.side"));
            tx.setQuantity(Double.parseDouble(record.get("transaction.quantity")));
            tx.setPrice(Double.parseDouble(record.get("transaction.price")));
            tx.setCurrency(record.get("transaction.currency"));
            tx.setGrossAmount(Double.parseDouble(record.get("transaction.grossAmount")));
            tx.setTradeType(record.get("transaction.tradeType"));
            tx.setExecutionVenue(record.get("transaction.executionVenue"));
            trade.setTransaction(tx);

            // --- Metadata ---
            CanonicalTrade.Metadata metadata = new CanonicalTrade.Metadata();
            String tradeDate = record.get("metadata.tradeDateTime");
            metadata.setTradeDateTime(ZonedDateTime.parse(tradeDate));

            metadata.setCorrelationId(record.get("metadata.correlationId"));
            metadata.setSourceMessageFormat(record.get("metadata.sourceMessageFormat"));
            metadata.setSourceMessageId(record.get("metadata.sourceMessageId"));

            Map<String, Object> additionalProps = new HashMap<>();
            for (String header : record.toMap().keySet()) {
                if (header.startsWith("metadata.additionalProperties.")) {
                    String key = header.substring("metadata.additionalProperties.".length());
                    additionalProps.put(key, record.get(header));
                }
            }
            metadata.setAdditionalProperties(additionalProps);

            trade.setMetadata(metadata);

            return trade;
        }
    }
}
