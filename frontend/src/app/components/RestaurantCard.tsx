import { Card, CardContent, CardHeader } from "./ui/card";
import { Badge } from "./ui/badge";
import { RatingComponent } from "./RatingComponent";
import { ImageWithFallback } from "./figma/ImageWithFallback";
import { MapPin, Clock, Phone } from "lucide-react";

export interface Restaurant {
  id: string;
  name: string;
  cuisine: string;
  description: string;
  rating: number;
  tags: string[];
  image: string;
  // location: string;
  // distance: number;
  // hours: string;ad
  // phone: string;
  // priceRange: string;
}

interface RestaurantCardProps {
  restaurant: Restaurant;
  onRatingChange: (restaurantId: string, rating: number) => void;
}

export function RestaurantCard({ restaurant, onRatingChange }: RestaurantCardProps) {
  return (
    <Card className="overflow-hidden hover:shadow-md transition-shadow gap-0">
      <CardHeader className="p-0">
        <ImageWithFallback
          src={restaurant.image}
          alt={restaurant.name}
          className="w-full h-48 object-cover"
        />
      </CardHeader>
      <CardContent className="px-4">
        <div className="space-y-1">
          <div>
            <h3 className="font-semibold">{restaurant.name}</h3>
            <p className="text-sm text-muted-foreground">{restaurant.cuisine}</p>
          </div>
          
          <p className="text-sm text-gray-600 line-clamp-2">{restaurant.description}</p>
          
          {/*<div className="grid grid-cols-2 gap-2 text-sm text-muted-foreground">*/}
          {/*  <div className="flex items-center space-x-1">*/}
          {/*    <MapPin className="h-3 w-3" />*/}
          {/*    <span>{restaurant.distance} mi</span>*/}
          {/*  </div>*/}
          {/*  <div className="flex items-center space-x-1">*/}
          {/*    <Clock className="h-3 w-3" />*/}
          {/*    <span>{restaurant.hours}</span>*/}
          {/*  </div>*/}
          {/*</div>*/}
          
          {/*<div className="flex items-center justify-between">*/}
          {/*  <span className="font-medium text-green-600">{restaurant.priceRange}</span>*/}
          {/*  <a*/}
          {/*    href={`tel:${restaurant.phone}`}*/}
          {/*    className="flex items-center space-x-1 text-sm text-muted-foreground hover:text-primary"*/}
          {/*  >*/}
          {/*    <Phone className="h-3 w-3" />*/}
          {/*    <span>{restaurant.phone}</span>*/}
          {/*  </a>*/}
          {/*</div>*/}
          
          <div className="flex flex-wrap gap-1">
            {restaurant.tags.slice(0, 3).map((tag) => (
              <Badge key={tag} className="text-xs ">
                {tag}
              </Badge>
            ))}
          </div>
          
          <div className="pt-2">
            <RatingComponent
              currentRating={restaurant.rating}
              onRatingChange={(rating) => onRatingChange(restaurant.id, rating)}
            />
          </div>
        </div>
      </CardContent>
    </Card>
  );
}