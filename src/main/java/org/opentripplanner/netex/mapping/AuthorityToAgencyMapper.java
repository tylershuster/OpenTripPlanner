package org.opentripplanner.netex.mapping;

import static org.opentripplanner.netex.mapping.support.NetexObjectDecorator.withOptional;

import org.opentripplanner.netex.mapping.support.FeedScopedIdFactory;
import org.opentripplanner.transit.model.organization.Agency;
import org.opentripplanner.transit.model.organization.AgencyBuilder;
import org.rutebanken.netex.model.Authority;

/**
 * NeTEx authority is mapped to OTP agency. An authority is defined as "A company or organisation
 * which is responsible for the establishment of a public transport service." In NeTEx this is not
 * the same as an operator. A dummy authority can be created if input data is missing an authority.
 */
class AuthorityToAgencyMapper {

  private final FeedScopedIdFactory idFactory;
  private final String timeZone;

  /**
   * This id is used to generate a "dummy" authority when the input data is not associated with an
   * authority. The OTP Model requires an agency to exist, while Netex do not.
   */
  private final String dummyAgencyId;

  AuthorityToAgencyMapper(FeedScopedIdFactory idFactory, String timeZone) {
    this.idFactory = idFactory;
    this.timeZone = timeZone;
    this.dummyAgencyId = "Dummy-" + timeZone;
  }

  /**
   * Map authority and time zone to OTP agency.
   */
  Agency mapAuthorityToAgency(Authority source) {
    AgencyBuilder target = Agency
      .of(idFactory.createId(source.getId()))
      .setName(source.getName().getValue())
      .setTimezone(timeZone);

    withOptional(
      source.getContactDetails(),
      c -> {
        target.setUrl(c.getUrl());
        target.setPhone(c.getPhone());
      }
    );
    return target.build();
  }

  /**
   * Create a new dummy agency with time zone set. All other values are set to "N/A" and id set to
   * {@code "Dummy-" + timeZone}.
   */
  Agency createDummyAgency() {
    return Agency
      .of(idFactory.createId(dummyAgencyId))
      .setName("N/A")
      .setTimezone(timeZone)
      .setUrl("N/A")
      .setPhone("N/A")
      .build();
  }

  String dummyAgencyId() {
    return dummyAgencyId;
  }
}
