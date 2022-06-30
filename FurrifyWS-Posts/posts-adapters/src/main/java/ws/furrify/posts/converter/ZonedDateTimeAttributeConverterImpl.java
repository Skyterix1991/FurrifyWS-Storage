package ws.furrify.posts.converter;

import ws.furrify.shared.converter.ZonedDateTimeAttributeConverter;

import javax.persistence.Converter;

@Converter(autoApply = true)
class ZonedDateTimeAttributeConverterImpl extends ZonedDateTimeAttributeConverter {
}
