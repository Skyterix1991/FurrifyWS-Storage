package ws.furrify.sources.converter;

import ws.furrify.shared.converter.ZonedDateTimeAttributeConverter;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class ZonedDateTimeAttributeConverterImpl extends ZonedDateTimeAttributeConverter {
}