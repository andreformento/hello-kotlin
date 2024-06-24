package com.andreformento.money.organization.share

import com.andreformento.money.organization.Organization
import com.andreformento.money.user.User
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class OrganizationShareTest {

    private val organization = Organization(id = UUID.randomUUID(), name = "name org", description = "desc")
    private val otherOrganization = Organization(id = UUID.randomUUID(), name = "other org", description = "a")

    private val ownerUser = User(id = UUID.randomUUID(), name = "owner", email = "o")
    private val ownerOrganizationShare = OrganizationShare(
        id = UUID.randomUUID(),
        organization = organization,
        user = ownerUser,
        role = Role.OWNER,
    )
    private val ownerOfOtherOrganizationShare = OrganizationShare(
        id = UUID.randomUUID(),
        organization = otherOrganization,
        user = ownerUser,
        role = Role.OWNER,
    )

    private val sharedUser = User(id = UUID.randomUUID(), name = "shared", email = "s")
    private val sharedOrganizationShare = OrganizationShare(
        id = UUID.randomUUID(),
        organization = organization,
        user = sharedUser,
        role = Role.ADMIN,
    )

    @Test
    fun `should allow remove self organization share when is not owner`() {
        assert(sharedOrganizationShare.denyReasonForNotDelete(sharedOrganizationShare) == null)
    }

    @Test
    fun `should deny remove self organization share when is owner`() {
        assert(ownerOrganizationShare.denyReasonForNotDelete(ownerOrganizationShare) == OwnerCannotRemoveYourOwnOrganization)
    }

    @Test
    fun `should allow remove organization share from other user when is owner`() {
        assert(sharedOrganizationShare.denyReasonForNotDelete(ownerOrganizationShare) == null)
    }

    @Test
    fun `should deny remove organization share from other user when is not owner`() {
        assert(ownerOrganizationShare.denyReasonForNotDelete(sharedOrganizationShare) == UserDonTHavePermissionToRemove)
    }

    @Test
    fun `should deny remove organization from other organization share`() {
        assert(ownerOrganizationShare.denyReasonForNotDelete(ownerOfOtherOrganizationShare) == UserDonTHavePermissionToRemove)
    }

    @Test
    fun `should allow share organization just by owner`() {
        assertTrue(ownerOrganizationShare.canBeShared())
        assertFalse(ownerOrganizationShare.copy(role = Role.ADMIN).canBeShared())
    }

}
