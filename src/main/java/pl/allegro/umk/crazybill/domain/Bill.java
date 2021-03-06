package pl.allegro.umk.crazybill.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.allegro.umk.crazybill.api.dto.BillDto;
import pl.allegro.umk.crazybill.api.dto.PositionDto;

import java.util.*;

@Document(collection = "bills")
public class Bill {
    @Id
    private String id;
    private String name;
    private List<BillPosition> positions;

    public Bill(String id, String name, List<BillPosition> positions) {
        this.id = id;
        this.name = name;
        this.positions = Collections.unmodifiableList(positions);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<BillPosition> getPositions() {
        return positions;
    }

    public static Bill fromDto(BillDto billDto) {
        List<BillPosition> positions = new ArrayList<>();
        for (PositionDto positionDto: billDto.getPositions()) {
            positions.add(BillPosition.fromDto(positionDto));
        }

        return new Bill(UUID.randomUUID().toString(), billDto.getName(), positions);
    }

    public BillDto toDto() {
        List<PositionDto> billPositions = new ArrayList<>();
        for (BillPosition position: positions) {
            billPositions.add(position.toDto());
        }
        return new BillDto(id, name, billPositions);
    }

    public static BillBuilder builder() {
        return new BillBuilder();
    }

    public static class BillBuilder {
        private String id;
        private String name;
        private List<BillPosition> positions = new ArrayList<>();

        public BillPositionBuilder paidFor(String name, double price) {
            return new BillPositionBuilder(this, name, price);
        }

        public Bill build() {
            return new Bill(id, name, positions);
        }

        public class BillPositionBuilder {
            private final BillBuilder billBuilder;
            private final String name;
            private final double price;

            public BillPositionBuilder(BillBuilder billBuilder, String name, double price) {
                this.billBuilder = billBuilder;
                this.name = name;
                this.price = price;
            }

            public BillBuilder by(String... persons) {
                return billBuilder.withId(id).withName(name).withPosition(new BillPosition(name, price, Arrays.asList(persons)));
            }
        }

        private BillBuilder withId(String id) {
            this.id = id;
            return this;
        }

        private BillBuilder withName(String name) {
            this.name = name;
            return this;
        }

        private BillBuilder withPosition(BillPosition billPosition) {
            positions.add(billPosition);
            return this;
        }
    }
}
