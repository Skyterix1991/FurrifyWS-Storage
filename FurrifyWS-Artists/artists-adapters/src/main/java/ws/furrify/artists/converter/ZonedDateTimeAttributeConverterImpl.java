package ws.furrify.artists.converter;

import ws.furrify.shared.converter.ZonedDateTimeAttributeConverter;

import javax.persistence.Converter;

@Converter(autoApply = true)
class ZonedDateTimeAttributeConverterImpl extends ZonedDateTimeAttributeConverter {
}
