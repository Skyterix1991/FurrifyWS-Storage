package ws.furrify.artists.converter;

import ws.furrify.shared.converter.UUIDAttributeConverter;

import javax.persistence.Converter;

@Converter(autoApply = true)
class UUIDAttributeConverterImpl extends UUIDAttributeConverter {
}
