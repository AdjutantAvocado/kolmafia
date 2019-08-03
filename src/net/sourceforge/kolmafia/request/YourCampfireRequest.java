/**
 * Copyright (c) 2005-2018, KoLmafia development team
 * http://kolmafia.sourceforge.net/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  [1] Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  [2] Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in
 *      the documentation and/or other materials provided with the
 *      distribution.
 *  [3] Neither the name "KoLmafia" nor the names of its contributors may
 *      be used to endorse or promote products derived from this software
 *      without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.kolmafia.request;

import java.util.Map;
import java.util.regex.Pattern;

import net.java.dev.spellcast.utilities.LockableListModel;

import net.sourceforge.kolmafia.AdventureResult;
import net.sourceforge.kolmafia.CoinmasterData;

import net.sourceforge.kolmafia.objectpool.ItemPool;

import net.sourceforge.kolmafia.persistence.CoinmastersDatabase;

import net.sourceforge.kolmafia.preferences.Preferences;

public class YourCampfireRequest
	extends CoinMasterRequest
{
	public static final String master = "Your Campfire";

	private static final LockableListModel<AdventureResult> buyItems = CoinmastersDatabase.getBuyItems( YourCampfireRequest.master );
	private static final Map<Integer, Integer> buyPrices = CoinmastersDatabase.getBuyPrices( YourCampfireRequest.master );
	private static Map<Integer, Integer> itemRows = CoinmastersDatabase.getRows( YourCampfireRequest.master );
	private static final Pattern FIREWOOD_PATTERN = Pattern.compile( "([\\d,]+) stick? of firewood" );
	public static final AdventureResult STICK_OF_FIREWOOD = ItemPool.get( ItemPool.STICK_OF_FIREWOOD, 1 );

	public static final CoinmasterData YOUR_CAMPFIRE =
		new CoinmasterData(
			YourCampfireRequest.master,
			"campfire",
			YourCampfireRequest.class,
			"stick of firewood",
			"no sticks of firewood",
			false,
			YourCampfireRequest.FIREWOOD_PATTERN,
			YourCampfireRequest.STICK_OF_FIREWOOD,
			null,
			YourCampfireRequest.itemRows,
			"shop.php?whichshop=campfire",
			"buyitem",
			YourCampfireRequest.buyItems,
			YourCampfireRequest.buyPrices,
			null,
			null,
			null,
			null,
			"whichrow",
			GenericRequest.WHICHROW_PATTERN,
			"quantity",
			GenericRequest.QUANTITY_PATTERN,
			null,
			null,
			true
		);

	public YourCampfireRequest()
	{
		super( YourCampfireRequest.YOUR_CAMPFIRE );
	}

	public YourCampfireRequest( final boolean buying, final AdventureResult [] attachments )
	{
		super( YourCampfireRequest.YOUR_CAMPFIRE, buying, attachments );
	}

	public YourCampfireRequest( final boolean buying, final AdventureResult attachment )
	{
		super( YourCampfireRequest.YOUR_CAMPFIRE, buying, attachment );
	}

	public YourCampfireRequest( final boolean buying, final int itemId, final int quantity )
	{
		super( YourCampfireRequest.YOUR_CAMPFIRE, buying, itemId, quantity );
	}

	@Override
	public void run()
	{
		if ( this.action != null )
		{
			this.addFormField( "pwd" );
		}

		super.run();
	}

	@Override
	public void processResults()
	{
		YourCampfireRequest.parseResponse( this.getURLString(), this.responseText );
	}

	public static void parseResponse( final String location, final String responseText )
	{
		if ( !location.contains( "whichshop=campfire" ) )
		{
			return;
		}

		CoinmasterData data = YourCampfireRequest.YOUR_CAMPFIRE;

		String action = GenericRequest.getAction( location );
		if ( action != null )
		{
			CoinMasterRequest.parseResponse( data, location, responseText );
			return;
		}

		// Parse current coin balances
		CoinMasterRequest.parseBalance( data, responseText );
	}

	public static String accessible()
	{
		return Preferences.getBoolean( "getawayCampsiteUnlocked" ) ? null : "Need access to your Getaway Campsite";
	}

	public static final boolean registerRequest( final String urlString )
	{
		if ( !urlString.startsWith( "shop.php" ) || !urlString.contains( "whichshop=campfire" ) )
		{
			return false;
		}

		CoinmasterData data = YourCampfireRequest.YOUR_CAMPFIRE;
		return CoinMasterRequest.registerRequest( data, urlString, true );
	}
}